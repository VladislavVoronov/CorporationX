#!/usr/bin/env python3
"""Start the built starter services against local infrastructure, one at a time."""
import argparse
import os
from pathlib import Path
import socket
import subprocess
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

ROOT = Path(__file__).resolve().parent.parent
SERVICES = ["user_service", "project_service", "post_service", "payment_service",
            "notification_service", "analytics_service", "achievement_service",
            "account_service", "url_shortener_service"]


def check(service):
    jar = ROOT / service / "build/libs/service.jar"
    if not jar.exists():
        raise RuntimeError(f"{service}: run ./scripts/check.sh first")
    logs = ROOT / ".starter-work/smoke"
    logs.mkdir(parents=True, exist_ok=True)
    log = logs / f"{service}.log"
    with socket.socket() as reservation:
        reservation.bind(("127.0.0.1", 0))
        port = reservation.getsockname()[1]
    java = str(Path(os.environ["JAVA_HOME"]) / "bin/java") if "JAVA_HOME" in os.environ else "java"
    with log.open("w") as output:
        process = subprocess.Popen([java, "-jar", str(jar), f"--server.port={port}",
                                    "--server.address=127.0.0.1"], cwd=ROOT / service,
                                   stdout=output, stderr=subprocess.STDOUT)
        try:
            deadline = time.monotonic() + 90
            while time.monotonic() < deadline:
                if process.poll() is not None:
                    raise RuntimeError(f"{service}: exited with {process.returncode}; see {log}")
                contents = log.read_text(errors="replace")
                if "Started " in contents and "seconds" in contents:
                    try:
                        with urlopen(Request(f"http://127.0.0.1:{port}/", headers={"x-user-id": "1"}), timeout=2) as response:
                            status = response.status
                    except HTTPError as error:
                        status = error.code
                    except (URLError, TimeoutError):
                        time.sleep(0.5)
                        continue
                    if status >= 500:
                        raise RuntimeError(f"{service}: HTTP {status}; see {log}")
                    print(f"PASS {service}: started, HTTP {status}", flush=True)
                    return
                time.sleep(0.5)
            raise RuntimeError(f"{service}: startup timeout; see {log}")
        finally:
            if process.poll() is None:
                process.terminate()
                try:
                    process.wait(timeout=15)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("services", nargs="*", help="Service directories; omit to check all")
    args = parser.parse_args()
    selected = args.services or SERVICES
    if any(service not in SERVICES for service in selected):
        parser.error("Unknown service")
    failures = []
    for service in selected:
        try:
            check(service)
        except RuntimeError as error:
            failures.append(service)
            print(f"FAIL {error}", flush=True)
    raise SystemExit(bool(failures))

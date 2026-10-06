FROM debian:bookworm-slim
ARG TARGETARCH
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl \
    && rm -rf /var/lib/apt/lists/*
RUN case "$TARGETARCH" in \
      arm64) checksum=5dbd09c7c784ba8d31b70338f0ed375e43b3b26ff00c726968ccaa97a3474d9e ;; \
      amd64) checksum=cff00c1937d6cdbc25e855b08c34be94d31e1d87f6b2615624ecba85f237aa06 ;; \
      *) echo "Supported architectures: arm64, amd64" >&2; exit 1 ;; \
    esac \
    && curl -fSL --retry 3 "https://github.com/minio/minio/releases/download/RELEASE.2023-09-04T19-57-37Z/minio_20230904195737.0.0_${TARGETARCH}.deb" -o /tmp/minio.deb \
    && echo "$checksum  /tmp/minio.deb" | sha256sum -c - \
    && dpkg -i /tmp/minio.deb \
    && ln -s "$(command -v minio)" /minio \
    && rm /tmp/minio.deb
EXPOSE 9000 9001
ENTRYPOINT ["/minio"]
CMD ["server", "/data", "--console-address", ":9001"]

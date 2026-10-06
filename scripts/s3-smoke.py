#!/usr/bin/env python3
"""Verify the local S3 API with the official AWS SDK; remove only this run's fixture."""
from uuid import uuid4
from urllib.request import urlopen

import boto3
from botocore.config import Config

client = boto3.client(
    "s3", endpoint_url="http://127.0.0.1:19000", region_name="us-east-1",
    aws_access_key_id="user", aws_secret_access_key="password",
    config=Config(signature_version="s3v4", s3={"addressing_style": "path"},
                  request_checksum_calculation="when_required",
                  response_checksum_validation="when_required"),
)
bucket = "starter-smoke-" + uuid4().hex
key = "example.txt"
payload = b"CorporationX S3 smoke test\n"
client.create_bucket(Bucket=bucket)
try:
    client.put_object(Bucket=bucket, Key=key, Body=payload)
    response = client.get_object(Bucket=bucket, Key=key)
    try:
        if response["Body"].read() != payload:
            raise RuntimeError("Downloaded content differs")
    finally:
        response["Body"].close()
    objects = client.list_objects_v2(Bucket=bucket)
    if key not in [item["Key"] for item in objects.get("Contents", [])]:
        raise RuntimeError("Uploaded object is absent from listing")
    url = client.generate_presigned_url("get_object", Params={"Bucket": bucket, "Key": key}, ExpiresIn=60)
    with urlopen(url, timeout=10) as response:
        if response.read() != payload:
            raise RuntimeError("Presigned download differs")
finally:
    try:
        client.delete_object(Bucket=bucket, Key=key)
    finally:
        client.delete_bucket(Bucket=bucket)
print("PASS S3: bucket, upload, download, listing, presigned URL and deletion")

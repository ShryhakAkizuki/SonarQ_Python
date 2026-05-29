import requests


def safe_request(url):
    return requests.get(url, timeout=5, verify=True)

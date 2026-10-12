#!/usr/bin/env python3
import http.server
import socketserver
import os
import sys

PORT = 3000
DIRECTORY = "/app/applet"

class APKHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def guess_type(self, path):
        if path.endswith(".apk"):
            return "application/vnd.android.package-archive"
        if path.endswith(".zip"):
            return "application/zip"
        return super().guess_type(path)

    def end_headers(self):
        if self.path.endswith(".apk") or self.path.endswith(".zip"):
            filename = os.path.basename(self.path)
            self.send_header("Content-Disposition", f'attachment; filename="{filename}"')
            self.send_header("Cache-Control", "no-cache, no-store, must-revalidate")
        super().end_headers()

    def do_GET(self):
        # If accessing root /, check if custom portal index.html exists, else serve index.html
        if self.path == "/" or self.path == "":
            self.path = "/download.html"
        return super().do_GET()

def run_server():
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), APKHTTPRequestHandler) as httpd:
        print(f"Serving APKs on port {PORT} from {DIRECTORY}")
        httpd.serve_forever()

if __name__ == "__main__":
    run_server()

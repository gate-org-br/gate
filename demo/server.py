#!/usr/bin/env python3

from http.server import HTTPServer, SimpleHTTPRequestHandler
import os
import urllib.parse

class CustomRequestHandler(SimpleHTTPRequestHandler):
    def do_GET(self):
        self.handle_request()

    def do_POST(self):
        self.handle_request()

    def do_DELETE(self):
        self.handle_request()

    def handle_request(self):
        self.send_head()

    def send_head(self):
        path = self.translate_path(self.path)

        if os.path.isdir(path):
            self.list_directory(path)
        elif os.path.exists(path):
            self.send_file(path)
        else:
            self.send_error(404, "File not found")

    def send_file(self, path):
        try:
            with open(path, 'rb') as file:
                self.send_response(200)
                self.send_header("Content-type", self.guess_type(path))
                self.end_headers()
                self.copyfile(file, self.wfile)
        except IOError:
            self.send_error(500, "Internal Server Error")

    def list_directory(self, path):
        try:
            self.send_response(200)
            self.send_header("Content-type", "text/html")
            self.end_headers()
            files = os.listdir(path)
            self.wfile.write("<html><body><h2>Files in this directory:</h2><ul>".encode())
            for file_name in files:
                file_path = os.path.join(path, file_name)
                if os.path.isfile(file_path):
                    self.wfile.write(f"<li><a href='{file_name}'>{file_name}</a></li>".encode())
            self.wfile.write("</ul></body></html>".encode())
        except IOError:
            self.send_error(500, "Internal Server Error")

    def translate_path(self, path):
        path = urllib.parse.unquote(path)
        path = os.path.normpath(path)
        return os.path.join(os.getcwd(), path.lstrip('/'))

if __name__ == '__main__':
    server_address = ('', 8000)
    httpd = HTTPServer(server_address, CustomRequestHandler)
    print(f"Serving on port {server_address[1]}...")
    httpd.serve_forever()


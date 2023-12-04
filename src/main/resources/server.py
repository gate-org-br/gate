from http.server import SimpleHTTPRequestHandler
import io

class MyHandler(SimpleHTTPRequestHandler):
    def do_POST(self):
        # Treat the POST request as if it were a GET request
        self.do_GET()

if __name__ == "__main__":
    from http.server import HTTPServer
    server_address = ("", 8000)
    httpd = HTTPServer(server_address, MyHandler)
    print("Server started on port 8000.")
    httpd.serve_forever()

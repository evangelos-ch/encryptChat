--- COM1028_EncryptChat ---
---- By: ec00727 ----

All code in this application has been written by myself.

This is a chat system that allows two users to chat with each other in an encrypted manner.
The encryption is end to end, implemented with the Advanced Encryption Standard (AES) Encryption algorithms, and the encryption key is generated via a Diffie Hellman key exchange.

In order to get started with using the system, start the chat server by running the Main class in the server package.
Then, to open up a chat client, run the Main class in the chat client.
Server IPs to connect to must be numerical and can not be domain names.
The IP that corresponds to a server running locally is 127.0.0.1, and that was used throughout testing this application.
When two clients are connected, one has to first press the Establish Secure Connection button and wait for the process to finish before chatting can begin.
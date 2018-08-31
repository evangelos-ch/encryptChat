# encryptChat
Encrypted chat system written in  Java with a JavaFX GUI. Originally started as a university Software Engineering assignment.
The encryption used is a Diffie-Helman Key Exchange done over two clients connected to a Server.

## Components
The system is composed of 3 units:
* The (chat) Client
* The (chat) Server
* The Requests module

The Server is responsible for being the point where Clients connect to, and only two Clients can connect to it simultainously. Clients communicate to each-other, through the server, using Requests, which are essentially contain any serializeable Java Object as well as an ENUM value which denotes what the object is intended for. When two clients are connected, either client can initiate the key exchange, and once they receive signal from the server that the exchange is over, they can exchange text messages.

## Technologies used
The application is written 100% in Java 8, and using the new standard GUI library, Java FX, with the theme being written in CSS.

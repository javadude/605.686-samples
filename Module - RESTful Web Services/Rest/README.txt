For an example of making REST calls from Kotlin, see SimpleRestClient

To run the server:

    gradlew appRun

Wait until you see
    15:40:33 INFO    http://localhost:8080/restserver

You can then browse to

    http://localhost:8080/restserver/todo

to see the data.

When you run the app, it will fetch data from the server

NOTE:
    If you run via an emulator, it should work as-is.
    If you run from a device, you'll need to make sure the device
        is on the same network as the server, get the ip address of the
        server (ipconfig on windows, ifconfig -a on linux/mac), and replace
        the server address in TodoContentProvider.onCreate()

(Note - this is a rather old version of the app, written in Java
using a Content Provider to fetch the data. Originally the todo example
had a content provider in front of the actual database code. This made it
simple to switch to a web-services approach, but I would only recommend
a content provider if you want to directly expose all data to other apps.
If you want to expose a subset of data, I'd recommend implementing a bound
service.)

zubat
=====

About
-----

An java application to access the API of amun. It is used to debug and control a 
website based on amun. This is the reference implementation howto access the 
api. So feel free to hack and extend.

Installation
-----

Because Amun uses Oauth for API authentication Zubat needs to obtain an token
and token secret in order to access the API. If you start Zubat and the config
doesnt contain an token and token secret it will start the authentication 
process and try to obtain such tokens. The configuration must contain an 
consumerKey and consumerSecret. You can look at the table amun_oauth to get an 
consumerKey and consumerSecret.

If the authentication process starts simply click the "Login" button and login
with your credentials to the Amun website and allow the application access.
If the authentication was successful the obtained token and token secret gets
written into the config file. Restart the application and you should be able
to work with Zubat.

Zubat uses the JavaFX Webview (based on Webkit) to display web content. You need 
at least Java 7u6 because since this release JavaFX is included.

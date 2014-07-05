Eclipse Plugin to manage VHost configuration (apache, hosts file) helping website developement with eclipse.

The principle is to automate a local server configuration when developing for web (php for instance). In general, to enable a web site on a local machine, you need to modify the hosts file, add a virtual host to the apache configuration with several options (mainly always the same) and finally restart your apache server.
Fortunately this plugin do all for you in just a click. 

# Install & Updates

Open the eclipse install manager using the project [updatesite](https://github.com/mgsx-dev/eclipse-plugin-apache/blob/master/updatesite) url.

This plugin have been tested on some configuration :
 * Windows XP with Easy-Php and Wamp
 * Linux Ubuntu with a classic apache2 installation
 * Windows 7 with xampp

# Features

 * Apache installation directory selection (saved with the workspace).
 * Project configuration saved with the project resource (local data).
 * Root site directory selection among projet folder or sub folders.
 * Server name for the project.
 * Hosts file automatically updated.
 * Apache configuration automatically updated.
 * Several options like logs, indexes, access restrictions ...

Here is a screenshot.

![capture](https://github.com/mgsx-dev/eclipse-plugin-apache/raw/master/images/capture.png)

# Troubleshootings

## Windows Vista and Windows 7

If you want the plugin to run properly with Windows 7 and Vista, that is allow the plugin to edit the hosts file, you need to disable some security protections.
To do so, you need to turn off the UAC (User Account Control) :
http://windows.microsoft.com/en-US/windows-vista/Turn-User-Account-Control-on-or-off

## Xampp and Wamp on Windows

Make sure apache running as a service, it's required by the plugin to start or shutdown apache.


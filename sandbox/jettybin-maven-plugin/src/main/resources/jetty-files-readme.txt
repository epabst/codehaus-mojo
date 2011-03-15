The jetty-files.sha1 file was created using the following unix/cygwin commandline ...

  find jetty-5.1.11 -type f -not -wholename "*.svn*" -exec sha1sum {} \; > jetty-files.sha1

Using sha1 to ensure that the source control does not mess with the original file contents.


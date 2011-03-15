server = "www.twdata.org"
user = "mrdon"
prompt = ":~>"

conn.connect(server, user);
conn.setTimeout(20000);
conn.waitFor(prompt);
conn.sendLine("top");
print "==== Top ====\n"
print conn.getLine()
print conn.getLine()
print conn.getLine()
print conn.getLine()
conn.send("^C")

conn.waitFor(prompt)
print "\n==== Disk Usage ====\n"
conn.sendLine("df")
while 1:
    id = conn.waitForMux(["\r\n", prompt])
    if id == 0 :
        print conn.lastLine()
    else:
        break
conn.disconnect()

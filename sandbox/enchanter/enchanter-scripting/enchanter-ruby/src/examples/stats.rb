server = "www.twdata.org"
user = "mrdon"
prompt = ":~>"

$conn.connect(server, user);
$conn.setTimeout(20000);
$conn.waitFor(prompt);
$conn.sendLine("top");
puts "==== Top ====\n"
puts $conn.getLine()
puts $conn.getLine()
puts $conn.getLine()
puts $conn.getLine()
$conn.send("^C")

$conn.waitFor(prompt)
puts "\n==== Disk Usage ====\n"
$conn.sendLine("df")
mux = java.lang.reflect.Array.newInstance(java.lang.String, 2);
mux[0] = "\r\n"
mux[1] = prompt
while (true) 
	id = $conn.waitForMux(mux)
	if id == 0 
		puts $conn.lastLine()
	else
		retry
	end
end
$conn.disconnect()

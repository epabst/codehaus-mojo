conn.connect( host )
conn.setDebug( 1 )
conn.sendLine("")
conn.setTimeout( 1000 )
conn.waitFor( "ogin:" )
conn.sendLine( username )
conn.waitFor( "assword:" )
conn.sendLine( password )
conn.waitFor( "0" )
conn.sendLine( "date" );
conn.waitFor( "0" )
conn.sendLine( "exit" )
conn.waitFor( "ogin:" )


var http = require('http');
var querystring = require('querystring');
var util = require('util');

var fs = require("fs");
var file = "food_tracker.db";
var exists = fs.existsSync(file);
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(file);

/**
 * This is the database creation function 
 * - checks if tables are already created
 * - adds initial test values
 */
db.serialize(function() {
	if(!exists) {
		console.log("Create database");
		db.run("CREATE TABLE Tracker(id INT, menu TEXT, data TEXT)");
		db.run("CREATE TABLE Users(id INT, name TEXT)");
		// save last ID into database
		db.run("INSERT INTO Tracker VALUES (1, 'Snitel', '20160204_203424')");
		db.run("INSERT INTO Tracker VALUES (1, 'Shaorma', '20160127_162512')");
                db.run("INSERT INTO Tracker VALUES (1, 'Burger', '20160205_145043')");
		db.run("INSERT INTO Tracker VALUES (1, 'Pizza', '20160206_175032')");
		db.run("INSERT INTO Tracker VALUES (1, 'Burger', '20160205_145043')");
		
		db.run("INSERT INTO Users VALUES (1, 'LAST_ID')");
		db.run("INSERT INTO Users VALUES (1, 'Irina')");
	} else {
		console.log("Database already created");
	}
});

//Lets define a port we want to listen to
const PORT=8080; 

/**
 * Function which handles all requests and send response
 * with two type of requests (from browser and from android client):
 *
 * - browser ('/' and '/show' urls)
 *   > inser user data (username)
 *   > see history for for requested username
 *
 * - android ('/register', '/new_menu' and '/history')
 *   > add new user in the database and associate the name with an ID
 *   > add food menu for that user in the database
 *   > send all history for the specified user to the client
 */
function handleRequest(request, response){
	console.log("handleRequest");
	switch(request.url) {
		case '/':
			console.log("From website");
			if (request.method == 'GET') {
				response.writeHead(200, "OK", {'Content-Type': 'text/html'});
				response.write('<html><head><title>Welcome</title></head><body>');
				response.write('<h1>Welcome to Food Tracker History!</h1>');
				response.write('<h2>Enter your name to view your food history</h2>');
				response.write('<form enctype="application/x-www-form-urlencoded" action="/show" method="post">');
				response.write('Name: <input type="text" name="username" /><br />');
				response.write('<input type="submit" value="Find my meals!"/>');
				response.write('</form></body></html');
				response.end();
			}
		case '/show':
			console.log("Show history");
			if (request.method == 'POST') {
				var fullBody = '';
				var json;
				request.on('data', function(chunk) {
					fullBody += chunk.toString();
				});
				request.on('end', function() {
					var data = querystring.parse(fullBody);

					console.log("History for " + data.username);

					db.all("SELECT * FROM Users WHERE name == (?)", [data.username], function (err, dbRes) {
                                                if (err) { throw err; }

						response.writeHead(200, "OK", {'Content-Type': 'text/html'});
                                		response.write('<html><head><title>History</title></head><body>');
                                		response.write('<h1>Here is the history for ' + data.username + '</h1>');

						db.all("SELECT menu, data FROM Tracker WHERE id == (?)", [dbRes[0].id], function (errr, result) {

							json = JSON.stringify(result, null, 4);
							console.log("FROM DB: %s", json);

							//response.writeHead(200, { 'Content-Type': 'test/json'});
							//response.write(json);
							for (var i = 0; i < result.length; i++) {
								response.write('<h2>' + result[i].menu + ' at time: ' + result[i].data + '</h2>');
							}
							response.end();
						});
                                        });

				});
			}
			break;
		case '/register':
			console.log("Register new user");
			if (request.method == 'POST') {
				console.log("Received request POST");
				
				var body = '';
				request.on('data', function(chunk) {
      					body += chunk;
    				});
				request.on('end', function() {
      					var data = querystring.parse(body);
					console.log("Name: %s", data.paramUsername);				
					
					// send id
					var x;
                                	db.each("SELECT id FROM Users WHERE name == 'LAST_ID'", function(err, id) {
                                        	var upd = db.prepare("UPDATE Users SET id = (?) WHERE name == 'LAST_ID'");
                                        	x = id.id + 1;
                                        	console.log("New id is ", x);
                                        	upd.run(x);
                                        	upd.finalize();

						// add user to database
						var add = db.prepare("INSERT INTO Users VALUES (?, ?)");
						add.run(x, data.paramUsername);
						add.finalize();

						var y = x + "";
						console.log("y=%s, x = %s",y, x);
						response.end(y);
                                	});        
				});
			}
			break;
		 case '/new_menu':
                        console.log("Add a new menu");
			if (request.method == 'POST') {
                                console.log("Received request POST");

                                var body = '';
                                request.on('data', function(chunk) {
                                        body += chunk;
                                });
                                request.on('end', function() {
                                        var data = querystring.parse(body);
					var id = data.paramId;
					var food = data.paramFood;
					var time = data.paramTime;
					console.log("Inset into database for id %s: food = %s, time = %s", id, food, time);					
					var upd = db.prepare("INSERT INTO Tracker VALUES (?, ?, ?)");
					upd.run(parseInt(id), food, time);
					upd.finalize();
                                });
				response.end('0');
                        }
                        break;
                case '/history':
                        console.log("Retrieve history");
                        if (request.method == 'POST') {
				console.log("Received request POST");

                                var body = '';
                                request.on('data', function(chunk) {
                                        body += chunk;
                                });
                                request.on('end', function() {
                                        var data = querystring.parse(body);
                                        var id = data.paramId;

					db.all("SELECT * FROM Tracker WHERE id == (?)", [parseInt(id)], function (err, result) {
                                                if (err) { throw err; }

                                                json = JSON.stringify(result);
                                                console.log("FROM DB: %s", json);

                                                response.end(json);
                                        });


                                });
                        }
                        break;
		default:
			response.writeHead(404, "Not found", {'Content-Type': 'text/html'});
			response.end('<html><head><title>404 - Not found</title></head><body><h1>Not found.</h1></body></html>');
			break;
	}
}

//Create a server
var server = http.createServer(handleRequest);

/** Start the Food tracking server */
server.listen(PORT, function(){
	//Callback triggered when server is successfully listening. Hurray!
	console.log("Server listening on: http://localhost:%s", PORT);
});

let express = require('express') //Modulo express
let app = express()
let swig = require('swig') // Modulo swig para render de las vistas

let bodyParser = require('body-parser'); // Parsear body de post a req.body.*
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

let fs = require("fs"); // Modulo fs para crear el server
let https = require('https'); // Modulo https

let mongodb = require('mongodb'); // Modulo mongodb
let gestorBD = require("./modules/gestorBD.js"); // Modulo gestorBD
gestorBD.init(app, mongodb);

let expressSession = require('express-session'); // Modulo sesion
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

app.use(express.static('public'));
let crypto = require('crypto'); // Modulo criptado de contraseña


//Variables
app.set('port', 8081) // Variable puerto 8081
app.set('db', 'mongodb://admin:sdi@tiendamusica-shard-00-00.essby.mongodb.net:27017,tiendamusica-shard-00-01.essby.mongodb.net:27017,tiendamusica-shard-00-02.essby.mongodb.net:27017/myWallapop?ssl=true&replicaSet=atlas-u3t42f-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('crypto', crypto); // Criptado de contraseña
app.set('clave', 'abcdefg'); // Criptado de contraseña

//Routers

//Router para identificar si el usuario esta logeado
var routerUsuarioSession = express.Router();
routerUsuarioSession.use(function (req, res, next) {
    if (req.session.usuario) {
        next();
    } else {
        res.redirect("/identificarse");
    }
});
app.use("/tienda", routerUsuarioSession);
app.use("/publicaciones", routerUsuarioSession);
app.use("/compras", routerUsuarioSession);
app.use("/listUsers", routerUsuarioSession);


//Router para identificar si el usuario es admin
var routerUsuarioAdmin = express.Router();
routerUsuarioSession.use(function (req, res, next) {
    if (req.session.admin) {
        next();
    } else {
        if (req.session.usuario)
            res.redirect("/");
        else
            res.redirect("/identificarse");
    }
});
app.use("/listUsers", routerUsuarioAdmin);

//Controllers
require("./routes/rhome.js")(app, swig);
require("./routes/rusers.js")(app, swig, gestorBD);
require("./routes/radmins.js")(app, swig, gestorBD);

//Server Launch
https.createServer({
    key: fs.readFileSync('certificates/alice.key'),
    cert: fs.readFileSync('certificates/alice.crt')
}, app).listen(app.get('port'), function () {
    console.log("Servidor activo")
})

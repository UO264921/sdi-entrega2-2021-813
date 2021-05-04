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
let jwt = require('jsonwebtoken');
let rest = require('request');
app.set('jwt', jwt);
app.set('rest', rest);

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
app.use("/user/tienda", routerUsuarioSession);
app.use("/user/publicaciones", routerUsuarioSession);
app.use("/user/compras", routerUsuarioSession);
app.use("/admin/listUsers", routerUsuarioSession);


//Router para identificar si el usuario es admin
var routerUsuarioAdmin = express.Router();
routerUsuarioAdmin.use(function (req, res, next) {
    if (req.session.admin) {
        next();
    } else {
        if (req.session.usuario)
            res.redirect("/");
        else
            res.redirect("/identificarse");
    }
});
app.use("/admin/listUsers", routerUsuarioAdmin);

//Router para identificar si el usuario no es admin
var routerUsuarioNoAdmin = express.Router();
routerUsuarioNoAdmin.use(function (req, res, next) {
    if (!req.session.admin) {
        next();
    } else {
        if (req.session.usuario)
            res.redirect("/");
        else
            res.redirect("/identificarse");
    }
});
app.use("/user/publicaciones/", routerUsuarioNoAdmin);
app.use("/user/tienda/", routerUsuarioNoAdmin);
app.use("/user/compras/", routerUsuarioNoAdmin);

//routerUsuarioAutor
let routerUsuarioAutor = express.Router();
routerUsuarioAutor.use(function (req, res, next) {
    let path = require('path');
    let id = path.basename(req.originalUrl);
    gestorBD.obtenerPublicaciones(
        {_id: mongodb.ObjectID(id)}, function (publicaciones) {
            if (publicaciones[0].autor === req.session.usuario) {
                next();
            } else {
                res.redirect("/user/publicaciones?mensaje=No eres el autor del producto&tipoMensaje=alert-danger");
            }
        })
});

app.use("/user/publicaciones/eliminar/", routerUsuarioAutor);

//routerUsuarioVendida
let routerUsuarioVendida = express.Router();
routerUsuarioVendida.use(function (req, res, next) {
    let path = require('path');
    let id = path.basename(req.originalUrl);
    gestorBD.obtenerVentas(
        {"productoID": id}, function (venta) {
            if (venta.length === 0) {
                next();
            } else {
                res.redirect("/?mensaje=El producto ya se ha vendido&tipoMensaje=alert-danger");
            }
        })
});

app.use("/user/tienda/comprar", routerUsuarioVendida);

//routerUsuarioNoAutor
let routerUsuarioNoAutor = express.Router();
routerUsuarioNoAutor.use(function (req, res, next) {
    let path = require('path');
    let id = path.basename(req.originalUrl);
    gestorBD.obtenerPublicaciones(
        {_id: mongodb.ObjectID(id)}, function (publicaciones) {
            if (publicaciones[0].autor !== req.session.usuario) {
                next();
            } else {
                res.redirect("/user/tienda?mensaje=Eres el vendedor del producto&tipoMensaje=alert-danger");
            }
        })
});

app.use("/user/tienda/comprar", routerUsuarioNoAutor);

// routerUsuarioToken
let routerUsuarioToken = express.Router();
routerUsuarioToken.use(function (req, res, next) {
    let token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        jwt.verify(token, 'secreto', function (err, infoToken) {
            if (err || (Date.now() / 1000 - infoToken.tiempo) > 240) {
                res.status(403); // Forbidden
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
                return;
            } else {
                res.usuario = infoToken.usuario;
                next();
            }
        });
    } else {
        res.status(403);
        res.json({
            acceso: false,
            mensaje: 'No hay Token'
        });
    }
});

// Aplicar routerUsuarioToken
app.use('/api/oferta', routerUsuarioToken);
app.use('/api/mensaje', routerUsuarioToken);
app.use('/api/misproductos', routerUsuarioToken);

//Controllers
require("./routes/rhome.js")(app, swig);
require("./routes/rusers.js")(app, swig, gestorBD);
require("./routes/radmins.js")(app, swig, gestorBD);
require("./routes/rpublicaciones.js")(app, swig, gestorBD);
require("./routes/rtienda.js")(app, swig, gestorBD);
require("./routes/rapiofertas.js")(app, gestorBD);

//Server Launch
https.createServer({
    key: fs.readFileSync('certificates/alice.key'),
    cert: fs.readFileSync('certificates/alice.crt')
}, app).listen(app.get('port'), function () {
    console.log("Servidor activo")
})

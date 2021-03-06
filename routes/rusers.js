module.exports = function (app, swig, gestorBD, logger) {

    // Vista de login de usuario
    app.get('/identificarse', function (req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', {})
        res.send(respuesta);
    })

    // Identificacion de usuario
    app.post("/identificarse", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        if (req.body.password.length < 1 || req.body.email.length < 1)
            res.redirect("/identificarse?mensaje=Todos los datos deben estar cubiertos&tipoMensaje=alert-danger");
        else {
            let criterio = {
                email: req.body.email,
                password: seguro
            }
            gestorBD.obtenerUsuarios(criterio, function (usuarios) {
                if (usuarios == null || usuarios.length == 0) {
                    gestorBD.obtenerAdmins(criterio, function (admins) {
                        if (admins == null || admins.length == 0) {
                            req.session.usuario = null;
                            res.redirect("/identificarse" +
                                "?mensaje=Email o contraseña incorrectos" +
                                "&tipoMensaje=alert-danger ");
                        } else {
                            logger.info("Inicio de sesion del admin: " + admins[0].email);
                            req.session.usuario = admins[0].email;
                            req.session.admin = true;
                            req.session.nombre = admins[0].nombre;
                            res.redirect("/");
                        }
                    })
                } else {
                    logger.info("Inicio de sesion del usuario: " + usuarios[0].email);
                    req.session.usuario = usuarios[0].email;
                    req.session.admin = false;
                    req.session.nombre = usuarios[0].nombre;
                    req.session.dinero = usuarios[0].dinero;
                    res.redirect("/");
                }
            });
        }
    });

    // Vista de registro de usuario
    app.get("/registrarse", function (req, res) {
        let respuesta = swig.renderFile('views/bregistro.html', {});
        res.send(respuesta);
    });

    // Registro de usuario
    app.post('/registrarse', function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        if (req.body.nombre.length < 1 || req.body.apellidos.length < 1
            || req.body.password.length < 1 || req.body.reppassword.length < 1 || req.body.email.length < 1)
            res.redirect("/registrarse?mensaje=Todos los datos deben estar cubiertos&tipoMensaje=alert-danger");
        else if (req.body.reppassword != req.body.password) {
            res.redirect("/registrarse?mensaje=Las contraseñas no coinciden&tipoMensaje=alert-danger");
        } else {
            let usuario = {
                email: req.body.email,
                password: seguro,
                nombre: req.body.nombre,
                apellidos: req.body.apellidos,
                dinero: 100
            }
            let criterio = {
                email: req.body.email
            }
            gestorBD.obtenerUsuarios(criterio, function (usuarios) {
                if (usuarios == null || usuarios.length == 0) {
                    gestorBD.insertarUsuario(usuario, function (id) {
                        if (id == null) {
                            res.redirect("/registrarse?mensaje=Error al registrarse&tipoMensaje=alert-danger");
                        } else {
                            res.redirect("/identificarse?mensaje=Te has registrado con exito");
                        }
                    });
                } else {
                    res.redirect("/registrarse?mensaje=Ya existe un usuario con ese email&tipoMensaje=alert-danger");
                }
            });
        }

    })

    // Desconexion del usuario
    app.get('/logout', function (req, res) {
        logger.info("Se ha desconectado el usuario: " + req.session.usuario);
        req.session.usuario = null;
        res.redirect("/identificarse");
    })

}
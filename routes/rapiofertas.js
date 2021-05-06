module.exports = function (app, gestorBD) {

    // Identificacion de usuario
    app.post("/api/autenticar", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        }
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(401);
                res.json({
                    autenticado: false
                })
            } else {
                let token = app.get('jwt').sign(
                    {usuario: criterio.email, tiempo: Date.now() / 1000},
                    "secreto");
                res.status(200);
                res.json({
                    autenticado: true,
                    token: token
                })
            }
        });
    });

    // Ofertas de la aplicacion
    app.get("/api/oferta", function (req, res) {
        gestorBD.obtenerPublicaciones({"autor": {$ne: res.usuario}}, function (ofertas) {
            if (ofertas == null) {
                res.status(500);
                res.json({
                    error: "se ha producido un error"
                })
            } else {
                res.status(200);
                res.send(JSON.stringify(ofertas));
            }
        });
    });

    // Productos del usuario
    app.get("/api/misproductos", function (req, res) {
        gestorBD.obtenerPublicaciones({"autor": res.usuario}, function (ofertas) {
            if (ofertas != null) {
                res.status(200);
                res.send(JSON.stringify(ofertas));
            }
        });
    })

    // Mensajes de la conversacion
    app.get("/api/mensaje/:id", function (req, res) {
        gestorBD.obtenerPublicaciones({"_id": gestorBD.mongo.ObjectId(req.params.id)}, function (oferta) {
            if (oferta != null) {
                gestorBD.obtenerMensajes({
                    $or: [{
                        "origen": res.usuario,
                        "idoferta": req.params.id
                    }, {
                        "destino": res.usuario,
                        "idoferta": req.params.id
                    }]
                }, function (mensajes) {
                    if (mensajes == null) {
                        res.status(500);
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        let lista = []
                        let mensaje = {}
                        for (var i in mensajes) {
                            if (mensajes[i].destino == res.usuario) {
                                lista.push(mensajes[i]._id)
                                mensajes[i].leido = "Leido"
                            }
                            mensaje = {
                                leido: "Leido"
                            }

                        }
                        if (lista.length > 0) {
                            gestorBD.updateLeido(lista, mensaje, function (m) {
                                if (m === null) {
                                    res.status(500);
                                    res.json({
                                        error: "se ha producido un error"
                                    })
                                }
                            })
                        }

                        res.status(200);
                        res.send(JSON.stringify(mensajes));
                    }
                });
            }
        })
    });

    // Envio de mensajes a la conversacion
    app.post("/api/mensaje/:id", function (req, res) {
        var today = new Date();
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
        var yyyy = today.getFullYear();
        var hh = today.getHours();
        var m = today.getMinutes();
        var order = today.getMilliseconds();
        today = mm + '/' + dd + '/' + yyyy + ' ' + hh + ':' + m;
        gestorBD.obtenerMensajes({"idoferta": req.params.id}, function (mensajes) {
            gestorBD.obtenerPublicaciones({"_id": gestorBD.mongo.ObjectId(req.params.id)}, function (oferta) {
                if (oferta != null) {
                    if (!mensajes[0] || mensajes[0].origen === res.usuario) {
                        let mensaje = {
                            origen: res.usuario,
                            idoferta: req.params.id,
                            fecha: today,
                            texto: req.body.texto,
                            destino: oferta[0].autor,
                            leido: "No leido",
                        }
                        gestorBD.insertarMensaje(mensaje, function (insertado) {
                            if (insertado != null) {
                                res.status(200);
                                res.redirect(req.get('referer'));
                            }
                        })
                    } else {
                        let mensaje = {
                            origen: res.usuario,
                            idoferta: req.params.id,
                            fecha: today,
                            texto: req.body.texto,
                            destino: mensajes[0].origen,
                            leido: "No leido",
                        }
                        gestorBD.insertarMensaje(mensaje, function (insertado) {
                            if (insertado != null) {
                                res.status(200);
                                res.redirect(req.get('referer'));
                            }
                        })
                    }

                }
            })
        })

    });
}
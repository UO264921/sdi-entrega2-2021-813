module.exports = function (app, swig, gestorBD, logger) {

    // Vista de la tienda de la aplicacion
    app.get("/user/tienda", function (req, res) {
        let criterio = {};
        var regex = new RegExp(["^.*", req.query.busqueda, ".*$"].join(""), "i");
        if (req.query.busqueda != null) {
            criterio = {"titulo": regex};
        }

        let pg = parseInt(req.query.pg);
        if (req.query.pg == null) {
            pg = 1;
        }

        gestorBD.obtenerProductosPg(criterio, pg, function (productos, total) {
            if (productos == null) {
                res.send("Error al listar ");
            } else {
                let ultimaPg = total / 5;
                if (total % 5 > 0) {
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = [];
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                let criterio1 = {}
                gestorBD.obtenerVentas(criterio1, function (ventas) {
                    let productosVendidos = []
                    ventas.forEach(element => productosVendidos.push(gestorBD.mongo.ObjectID(element.productoID.toString()).toString()))
                    let respuesta = swig.renderFile('views/btienda.html',
                        {
                            productos: productos,
                            paginas: paginas,
                            actual: pg,
                            usuario: req.session.usuario,
                            admin: req.session.admin,
                            dinero: req.session.dinero,
                            productosVendidos: productosVendidos
                        });
                    res.send(respuesta);
                })
            }
        });
    });

    // Compra de una oferta
    app.get("/user/tienda/comprar/:id", function (req, res) {
        let productoID = gestorBD.mongo.ObjectID(req.params.id);
        gestorBD.obtenerPublicaciones({"_id": productoID}, function (publicacion) {
            if (publicacion == null) {
                res.redirect("/user/tienda?mensaje=Error en la compra&tipoMensaje=alert-danger");
            } else {
                let venta = {
                    usuario: req.session.usuario,
                    productoID: productoID.toString(),
                    titulo: publicacion[0].titulo,
                    detalle: publicacion[0].detalle,
                    precio: publicacion[0].precio,
                    autor: publicacion[0].autor
                }
                if (parseFloat(req.session.dinero) - parseFloat(publicacion[0].precio) < 0) {
                    res.redirect("/user/tienda?mensaje=No tienes suficiente dinero&tipoMensaje=alert-danger");
                } else {
                    req.session.dinero = "" + parseFloat(req.session.dinero) - parseFloat(publicacion[0].precio);
                    let user = {
                        dinero: req.session.dinero
                    }
                    gestorBD.insertarVenta(venta, function (idCompra) {
                        if (idCompra == null) {
                            res.redirect("/user/tienda?mensaje=Error en la compra&tipoMensaje=alert-danger");
                        } else {
                            gestorBD.updateDineroUsuario({"email": req.session.usuario}, user, function (updated) {
                                if (updated == null) {
                                    res.redirect("/user/tienda?mensaje=Error en la compra&tipoMensaje=alert-danger");
                                } else {
                                    logger.info("Se ha comprado una oferta por el usuario: " + req.session.usuario);
                                    res.redirect("/user/compras?mensaje=Se ha realizado la compra correctamente");
                                }
                            })
                        }
                    });
                }
            }
        })
    });

    // Vista de las compras del usuario
    app.get("/user/compras/", function (req, res) {
        let criterio = {"usuario": req.session.usuario}
        gestorBD.obtenerVentas(criterio, function (compras) {
            if (compras == null) {
                res.redirect("/user/tienda/compras?mensaje=Ha habido un error en la compra&tipoMensaje=alert-danger")
            } else {
                let respuesta = swig.renderFile('views/bcompras.html',
                    {
                        compras: compras,
                        usuario: req.session.usuario,
                        admin: req.session.admin,
                        dinero: req.session.dinero
                    });
                res.send(respuesta);
            }
        })
    });

}
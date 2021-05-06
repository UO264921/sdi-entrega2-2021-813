module.exports = function (app, swig, gestorBD, logger) {

    // Listado de usuarios
    app.get('/admin/listUsers', function (req, res) {
        let criterio = {}
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            usuarios = usuarios.sort((a, b) => (a.nombre > b.nombre) ? 1 : ((b.nombre > a.nombre) ? -1 : 0))
            let respuesta = swig.renderFile('views/blistUsers.html', {
                usuarios: usuarios,
                usuario: req.session.usuario,
                admin: req.session.admin
            })
            res.send(respuesta);
        })
    })

    // Eliminación de usuarios por ids
    app.get('/admin/eliminar/:id', function (req, res) {
        var parameters = req.params.id.split(",")
        let criterio = {}
        let criterios = []
        let emails = []
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            usuarios = usuarios.sort((a, b) => (a.nombre > b.nombre) ? 1 : ((b.nombre > a.nombre) ? -1 : 0))
            for (var i in parameters) {
                criterios.push(usuarios[parameters[i]]._id)
                emails.push(usuarios[parameters[i]].email)
            }
            gestorBD.eliminarUsuario(criterios, function (eliminados) {
                if (eliminados == null) {
                    res.redirect("/admin/listUsers?mensaje=Error en el borrado de usuarios&tipoMensaje=alert-danger");
                } else {
                    gestorBD.eliminarPublicacionesDeUsuarios(emails, function (publicaciones) {
                        if (publicaciones == null) {
                            res.redirect("/admin/listUsers?mensaje=Error en el borrado de publicaciones&tipoMensaje=alert-danger");
                        } else {
                            gestorBD.eliminarMensajesDeUsuarios(emails, function (mensajes) {
                                if (mensajes == null) {
                                    res.redirect("/admin/listUsers?mensaje=Error en el borrado de mensajes&tipoMensaje=alert-danger");
                                } else {
                                    gestorBD.eliminarMensajesAUsuarios(emails, function (mensajes) {
                                        if (mensajes == null) {
                                            res.redirect("/admin/listUsers?mensaje=Error en el borrado de mensajes&tipoMensaje=alert-danger");
                                        } else {
                                            gestorBD.eliminarComprasDeUsuarios(emails, function (compras) {
                                                if (compras == null) {
                                                    res.redirect("/admin/listUsers?mensaje=Error en el borrado de mensajes&tipoMensaje=alert-danger");
                                                } else {
                                                    logger.info("Se ha/n eliminado el/los usuario/s: " + emails + " por el administrador: " + req.session.usuario);
                                                    res.redirect("/admin/listUsers?mensaje=Éxito en el borrado de usuarios");
                                                }
                                            })
                                        }
                                    })
                                }
                            })
                        }
                    })
                }
            })
        })
    })
}
module.exports = function (app, swig, gestorBD, logger) {

    // Obtener ofertas
    app.get('/user/publicaciones', function (req, res) {
        let criterio = {autor: req.session.usuario}
        gestorBD.obtenerPublicaciones(criterio, function (publicaciones) {
            publicaciones = publicaciones.sort((a,b) => (a.titulo > b.titulo) ? 1 : ((b.titulo > a.titulo) ? -1 : 0))
            let respuesta = swig.renderFile('views/bpublicaciones.html', {
                publicaciones: publicaciones,
                usuario: req.session.usuario,
                admin: req.session.admin,
                dinero: req.session.dinero
            })
            res.send(respuesta);
        })
    })

    // Vista para publicar oferta
    app.get('/user/publicaciones/publicar', function (req, res) {
        let respuesta = swig.renderFile('views/bpublicar.html', {
            usuario: req.session.usuario,
            admin: req.session.admin,
            dinero: req.session.dinero
        })
        res.send(respuesta);
    })

    // Publicacion de oferta
    app.post('/user/publicaciones/publicar', function (req, res) {
        if (req.body.titulo.length < 3)
            res.redirect("/user/publicaciones/publicar?mensaje=El titulo debe tener como minimo 2 caracteres&tipoMensaje=alert-danger");
        else if (req.body.detalle.length < 6)
            res.redirect("/user/publicaciones/publicar?mensaje=El detalle debe tener como minimo 5 caracteres&tipoMensaje=alert-danger");
        else if (req.body.precio <= 0)
            res.redirect("/user/publicaciones/publicar?mensaje=El precio debe ser mayor de 0&tipoMensaje=alert-danger");
        else {
            let publicacion = {
                titulo: req.body.titulo,
                detalle: req.body.detalle,
                fecha: new Date().toJSON().slice(0, 10).replace(/-/g, '/'),
                precio: req.body.precio,
                autor: req.session.usuario,
            }

            gestorBD.insertarPublicacion(publicacion, function (id) {
                if (id == null) {
                    res.send("Error al insertar la publicacion");
                } else {
                    logger.info("Se ha publicado una oferta por el usuario: " + req.session.usuario);
                    res.redirect("/user/publicaciones");
                }
            })
        }
    })

    // Eliminacion de publicacion
    app.get('/user/publicaciones/eliminar/:id', function (req, res) {
        let criterio = {_id: gestorBD.mongo.ObjectID(req.params.id)}
        gestorBD.eliminarPublicaciones(criterio, function (publicaciones) {
            if (publicaciones == null)
                res.redirect("/user/publicaciones?mensaje=Error al eliminar&tipoMensaje=alert-danger");
            else {
                logger.info("Se ha eliminado una oferta por el usuario: " + req.session.usuario);
                res.redirect("/user/publicaciones?mensaje=Se ha eliminado correctamente");
            }})
    })

}
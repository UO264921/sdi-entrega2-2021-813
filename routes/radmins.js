module.exports = function (app, swig, gestorBD) {
    app.get('/admin/listUsers', function (req, res) {
        let criterio = {}
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            let respuesta = swig.renderFile('views/blistUsers.html', {
                usuarios: usuarios,
                usuario: req.session.usuario,
                admin: req.session.admin
            })
            res.send(respuesta);
        })
    })

    app.get('/admin/eliminar/:id', function (req, res) {
        var parameters = req.params.id.split(",")
        let criterio = {}
        let criterios = []
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            for (var i in parameters) {
                criterios.push(usuarios[i]._id)
            }
            gestorBD.eliminarUsuario(criterios, function (eliminados, next) {
                if (eliminados == null) {
                    res.redirect("/listUsers?mensaje=Error en el borrado&tipoMensaje=alert-danger");
                } else {
                    res.redirect("/listUsers?mensaje=Éxito en el borrado de usuarios");
                }
            })
        })
    })
}
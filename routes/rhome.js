module.exports = function(app, swig){

    // Home
    app.get('/', function(req, res){
        let respuesta = swig.renderFile('views/bhome.html', {
            usuario: req.session.usuario,
            admin: req.session.admin,
            nombre: req.session.nombre,
            dinero: req.session.dinero
        })
        res.send(respuesta);
    })
}
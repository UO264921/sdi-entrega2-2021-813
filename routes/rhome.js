module.exports = function(app, swig){
    app.get('/', function(req, res){
        let respuesta = swig.renderFile('views/bhome.html', {
            usuario: req.session.usuario,
            nombre: req.session.nombre
        })
        res.send(respuesta);
    })
}
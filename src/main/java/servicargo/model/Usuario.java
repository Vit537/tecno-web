package servicargo.model;

public class Usuario {
    private final Integer id;
    private final String ci;
    private final String nombre;
    private final String apellido;
    private final String rol;
    private final String telefono;
    private final String email;
    private final String password;

    public Usuario(Integer id, String ci, String nombre, String apellido, String rol,
                   String telefono, String email, String password) {
        this.id = id;
        this.ci = ci;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.telefono = telefono;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public String getCi() {
        return ci;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getRol() {
        return rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

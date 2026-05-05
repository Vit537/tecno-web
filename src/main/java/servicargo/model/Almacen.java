package servicargo.model;

public class Almacen {
    private final Integer id;
    private final String nombre;
    private final String direccion;
    private final int capacidad;
    private final String responsable;
    private final String telefono;

    public Almacen(Integer id, String nombre, String direccion, int capacidad,
                   String responsable, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.responsable = responsable;
        this.telefono = telefono;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getResponsable() {
        return responsable;
    }

    public String getTelefono() {
        return telefono;
    }
}

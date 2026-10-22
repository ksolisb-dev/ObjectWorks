package objectworks.modelo;

import java.util.ArrayList;

public class Lista<T> {
    protected ArrayList<T> lista;

    public Lista() {
        lista = new ArrayList<>();
    }

    public void add(T elemento) { lista.add(elemento); }
    public void addAll(Lista<T> elementos) { lista.addAll(elementos.getArrayList()); }
    public void borrar(T elemento) { lista.remove(elemento); }
    public T getAt(int posicion) { return lista.get(posicion); }
    public int size() { return lista.size(); }
    public boolean isEmpty() { return lista.isEmpty(); }
    public void clear() { lista.clear(); }
    public boolean contains(T elemento) { return lista.contains(elemento); }
    public ArrayList<T> getArrayList() { return new ArrayList<>(lista); }
}
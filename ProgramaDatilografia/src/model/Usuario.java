package model;

public abstract class Usuario {

    private long id;
    private String nome;
    private int idade;

    public Usuario(long id, String nome, int idade) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
    }

    public void login() {
        System.out.println(nome + " fez login.");
    }

    public void logout() {
        System.out.println(nome + " fez logout.");
    }

    public long getId()     {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public int getIdade()   {
        return idade;
    }
}
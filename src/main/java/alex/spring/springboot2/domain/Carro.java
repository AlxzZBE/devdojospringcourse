package alex.spring.springboot2.domain;

public class Carro {
    private String name;

    public Carro() {
    }

    public Carro(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

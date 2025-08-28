package carsmartfactory.domain;

public enum Category {
    PRESS("프레스 공정"),
    WELDING("차체 공정"),
    PAINTING("도장 공정"),
    ASSEMBLY("의장 조립 공정");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
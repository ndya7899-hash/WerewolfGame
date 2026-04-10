public class Player {
    private String name;
    private String role;
    private String side; 
    private boolean isAlive;
    private boolean hasSkill = true; 

    public Player(String name, String role, String side) {
        this.name = name;
        this.role = role;
        this.side = side;
        this.isAlive = true;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getSide() { return side; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public boolean canUseSkill() { return hasSkill; }
    public void useSkill() { this.hasSkill = false; }
}
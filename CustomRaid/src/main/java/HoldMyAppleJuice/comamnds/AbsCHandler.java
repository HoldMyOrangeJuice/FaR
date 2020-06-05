package HoldMyAppleJuice.comamnds;

public abstract class AbsCHandler
{
    public abstract String handleGet();
    public abstract String handleSet();
    public abstract Integer getRequiredSetArgs();
    public abstract Integer getRequiredGetArgs();
}

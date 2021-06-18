
public class Square{
    private String boxname;
    private int height;
    private int width;
    private String boxcolor;
    private boolean outlined;

    public String getname() {
        return this.boxname;
    }

    public int getheight() {
        return this.height;
    }

    public int getWidth()
    {
        return this.width;
    }

    public String getColor()
    {
        return this.boxcolor;
    }
    
    public boolean outlined()
    {
        return this.outlined;
    }
    
    public void setname(String target) {
        this.boxname = target;
    }

    public void setheight(int target) {
        this.height = target;
    }

    public void setWidth(int target)
    {
        this.width = target;
    }

    public void setColor(String target)
    {
        this.boxcolor = target;
    }
    
    public void outlined(boolean target)
    {
        this.outlined = target;
    }
}
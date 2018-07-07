package tju.MSchedule.moudles;

public class Name {

    private String name;

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {

        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;

        Name objName = (Name)obj;
        if(name == null){

            if(objName.name != null){
                return false;
            }
        }else if(!name.equals(objName.name)) {
            return false;
        }

        return true;
    }
}

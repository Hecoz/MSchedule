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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
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

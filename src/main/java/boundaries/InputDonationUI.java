package boundaries;

import entities.User;

public class InputDonationUI extends UI{
    public InputDonationUI(User user) {
        super(user);
    }

    @Override
    public void showUI() {
        //TODO
        System.out.println("InputDonationUI");
    }

    public void displayStatus(String msg){
        //TODO
        System.out.println(msg);
    }

    public void success(String msg){
        //TODO
        System.out.println("Success" + msg);
    }

    public void error(String msg){
        //TODO
        System.out.println("Error" + msg);
    }
}

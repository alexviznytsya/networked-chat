/**
 * Created by sean_martinelli on 11/21/17.
 */

import controller.ClientController;

public class NetworkedChatClient
{
    public static void  main(String[] args)
    {
        ClientController clientController = new ClientController();
        clientController.start();
    }
}

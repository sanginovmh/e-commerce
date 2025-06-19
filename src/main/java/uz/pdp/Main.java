package uz.pdp;

import uz.pdp.model.Order;
import uz.pdp.model.User;
import uz.pdp.renderer.OrderRenderer;
import uz.pdp.service.OrderService;
import uz.pdp.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws Exception {


        OrderService orderService = new OrderService();
        System.out.println(OrderRenderer.render(orderService.getAll()));
    }
}
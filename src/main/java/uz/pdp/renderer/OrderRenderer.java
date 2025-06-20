package uz.pdp.renderer;

import lombok.RequiredArgsConstructor;
import uz.pdp.model.Order.BoughtItem;
import uz.pdp.model.Order;

import java.util.List;

@RequiredArgsConstructor
public final class OrderRenderer {
    public static String render(List<Order> list) {
        StringBuilder sb = new StringBuilder();
        for (Order order : list) {
            sb.append(render(order));
        }

        return sb.toString();
    }

    public static String render(Order order) {
        return String.format("%-13s -> %s:\n",
                order.getSeller().getUsername(),
                order.getCustomer().getUsername()) +
                renderBoughtItems(order.getBoughtItems()) +
                String.format("Grand Total: %.2f\n",
                        order.getGrandTotal());
    }

    private static StringBuilder renderBoughtItems(List<BoughtItem> list) {
        StringBuilder sb = new StringBuilder();
        for (BoughtItem item : list) {
            sb.append(String.format("%-10s $%-7.2f %-4d ->   $%-7.2f\n",
                    item.getProduct(),
                    item.getPricePerPsc(),
                    item.getAmountBought(),
                    item.getTotalPaid()));
        }

        return sb;
    }
}

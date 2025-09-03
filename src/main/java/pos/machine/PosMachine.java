package pos.machine;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import pos.machine.ReceiptItem;

public class PosMachine {
    // ========== Step 1. 数据库加载 ==========
    private Map<String, Item> loadItemDatabase() {
        return ItemsLoader.loadAllItems().stream().collect(Collectors.toMap(Item::getBarcode, item -> item));
    }

    // ========== Step 2. 校验 ==========
    private boolean validateBarcodes(List<String> barcodes) {
        Set<String> validBarcodes = loadItemDatabase().keySet();
        return barcodes.stream().allMatch(validBarcodes::contains);
    }

    // ========== Step 3. 统计数量 ==========
    private Map<String, Integer> countBarcodes(List<String> barcodes) {
        return barcodes.stream()
                .collect(Collectors.toMap(code -> code, code -> 1, Integer::sum));
    }

    // ========== Step 4. 匹配商品 ==========
    private List<ReceiptItem> matchItems(Map<String, Integer> barcodeCount) {
        Map<String, Item> itemDatabase = loadItemDatabase();
        return barcodeCount.entrySet()
                .stream()
                .map(entry -> {
                    Item item = itemDatabase.get(entry.getKey());
                    return new ReceiptItem(item.getName(), entry.getValue(), item.getPrice());
                })
                .collect(Collectors.toList());
    }

    // ========== Step 5. 计算总价 ==========
    private int calculateTotal(List<ReceiptItem> receiptItems) {
        return receiptItems.stream()
                .mapToInt(ReceiptItem::getSubtotal)
                .sum();
    }

    // ========== Step 6. 格式化输出 ==========
    private String formatReceipt(List<ReceiptItem> receiptItems, int total) {
        StringBuilder sb = new StringBuilder();
        sb.append("***<store earning no money>Receipt***\n");

        receiptItems.forEach(item ->
                sb.append(String.format("Name: %s, Quantity: %d, Unit price: %d (yuan), Subtotal: %d (yuan)\n",
                        item.getName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal()))
        );

        sb.append("----------------------\n");
        sb.append(String.format("Total: %d (yuan)\n", total));
        sb.append("**********************");
        return sb.toString();
    }
    public String printReceipt(List<String> barcodes) {
        if (!validateBarcodes(barcodes)) {
            System.out.println("输入的条码中包含数据库中不存在的商品！");
            return null;
        }

        Map<String, Integer> barcodeCount = countBarcodes(barcodes);
        List<ReceiptItem> receiptItems = matchItems(barcodeCount);
        int total = calculateTotal(receiptItems);

        return formatReceipt(receiptItems, total);
    }
}

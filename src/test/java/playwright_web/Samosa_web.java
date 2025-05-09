package playwright_web;
import com.microsoft.playwright.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Samosa_web {
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

             // Navigate to the website
            page.navigate("https://samosaparty.in/");
 
            // Selected 'Hyderabad' from the city dropdown
            Locator city_drop = page.locator("#citySelector").nth(0);
            		city_drop.waitFor();
            		city_drop.selectOption("Hyderabad");
            		Thread.sleep(5000);
 
            Locator local_drop = page.locator("div.outlet-div select#outletSelector").first();
            local_drop.waitFor();
 
            // Selected 'Ameerpet' from the locality dropdown
            local_drop.selectOption("Ameerpet");
            
            Thread.sleep(5000);
 
            // Select "Veg" 
            page.locator("span div.veg-flag").nth(0).click(); 
            
            // Wait for food items to load (based on how the site populates the items)
            //page.locator(".food-item-class").waitFor(); // Replace with actual class or identifier for food items
 
            // Capture the list of food items (name, price, and image)
            List<String> foodNames = page.locator(".item-title").allTextContents();  // Replace with actual class
            List<String> foodPrices = page.locator("xpath =//p[contains(@class,'pric e-p')]").allTextContents(); // Replace with actual class
            List<ElementHandle> foodImages = page.locator("xpath =//div[contains(@id,'item')]/img").elementHandles();
            List<String> imageUrls = new ArrayList<String>();

            for (ElementHandle img : foodImages) {
                String src = img.getAttribute("src");
                imageUrls.add(src);
            }
     
 
            // Output the captured data
            for (int i = 0; i < foodNames.size(); i++) {
                System.out.println("Food Item: " + foodNames.get(i));
                System.out.println("Price: " + foodPrices.get(i));
                System.out.println("Image URL: " + imageUrls.get(i));
            }
 
            // Write the captured data to Excel
            writeDataToExcel(foodNames, foodPrices, imageUrls);
 
            // Close the browser
            browser.close();
        }
    }
 
    public static void writeDataToExcel(List<String> foodNames, List<String> foodPrices, List<String> foodImages) throws IOException {
        // Create a workbook and a sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Food Items");
 
        // Create the header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Food Item Name");
        headerRow.createCell(1).setCellValue("Price");
        headerRow.createCell(2).setCellValue("Image URL");
 
        // Write data rows
        for (int i = 0; i < foodNames.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(foodNames.get(i));
            dataRow.createCell(1).setCellValue(foodPrices.get(i));
            dataRow.createCell(2).setCellValue(foodImages.get(i));
        }
 
        // Write the data to a file
        try (FileOutputStream fileOut = new FileOutputStream("food_items_final.xlsx")) {
            workbook.write(fileOut);
        }
        // Close the workbook
       workbook.close();
    }

}

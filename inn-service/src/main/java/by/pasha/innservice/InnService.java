package by.pasha.innservice;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class InnService {

    public  String generateIndividualINN() {
        Random random = new Random();
        // Первые 10 цифр - случайные
        int[] inn = new int[12];
        for (int i = 0; i < 10; i++) {
            inn[i] = random.nextInt(10);
        }

        // Расчет первой контрольной цифры (11-я позиция)
        inn[10] = calculateControlSum1(inn);

        // Расчет второй контрольной цифры (12-я позиция)
        inn[11] = calculateControlSum2(inn);

        return arrayToString(inn);
    }

    private  int calculateControlSum1(int[] inn) {

        int[] coefficients = {7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            sum += inn[i] * coefficients[i];
        }

        return sum % 11 % 10;
    }

    private  int calculateControlSum2(int[] inn) {
        int[] coefficients = {3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum = 0;

        for (int i = 0; i < 11; i++) {
            sum += inn[i] * coefficients[i];
        }

        return sum % 11 % 10;
    }

    private  String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int digit : array) {
            sb.append(digit);
        }
        return sb.toString();
    }
}

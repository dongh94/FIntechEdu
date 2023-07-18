package bank.card.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class NewsEntity {
    private String title;
    private String journalist;
    private String publisher;
    private String reg_dt;
}

package eus.klimu.klimudesktop.app.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatePeriod implements Serializable {

    private Date startDate;
    private Date endDate;

    public boolean check() {
        return startDate.before(endDate);
    }

}

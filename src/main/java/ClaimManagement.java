import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClaimManagement {
    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext initialContext = new InitialContext();
        Queue claimQueue = (Queue) initialContext.lookup("queue/claimQueue");

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
             JMSContext context = connectionFactory.createContext()) {


            JMSProducer producer = context.createProducer();
            /*
            Примеры фильтров
            claimAmount = 1000
            claimAmount BETWEEN 1000 AND 5000
            doctorName LIKE 'D%'
            doctorName LIKE 'D%c'
            doctorType IN ('neuro','psych')

             */
            JMSConsumer consumer = context.createConsumer(claimQueue, "doctorName LIKE 'P%' OR JMSPriority BETWEEN 3 AND 6");

            ObjectMessage objectMessage = context.createObjectMessage();

            Claim claim = new Claim(1,
                    "Doc",
                    "Tor",
                    "Reso",
                    1000
            );
/*
Создавая объект-сообщение, мы указываем ему свойство, и при получении,
мы можем, на SQL подобном языке, фильтроваться по этим свойствам
 */
            objectMessage.setObject(claim);
            // objectMessage.setIntProperty("hospitalId", 1); //claim.getHospitalId() тоже вариант
            //objectMessage.setDoubleProperty("claimAmount", 1000);
            objectMessage.setStringProperty("doctorName", "Doc");
            producer.send(claimQueue, objectMessage);

            Claim receivedClaim = consumer.receiveBody(Claim.class);
            System.out.println(receivedClaim);
        }
    }
}

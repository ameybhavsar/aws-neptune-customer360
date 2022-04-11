package app.neptune;

import org.neo4j.internal.helpers.collection.Iterators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeptuneQueries {
    
    // Used by all Usecase
    public static final String FIND_NODE_BY_VALUE =
            "MATCH (node)-[r]-(node2) WHERE ID(node) = '%s' RETURN ID(node) AS id, LABELS(node) AS labels, node.value AS value, ID(node2) AS id2, LABELS(node2) AS labels2, node2.value AS value2, TYPE(r) AS type";

    // Usecase 1: Customer logs into the financial portal to know about his financial landscape.
    public static final String GET_RELATED_BY_ID =
            //"MATCH (node)-[r]-(node2) WHERE ID(node) = '%s' RETURN ID(node) AS id, LABELS(node) AS labels, node.value AS value, ID(node2) AS id2, LABELS(node2) AS labels2, node2.value AS value2, TYPE(r) AS type";
            "MATCH (node:Customer)-[r]-(node2) WHERE node.value = '%s' RETURN ID(node) AS id, LABELS(node) AS labels, node.value AS value, ID(node2) AS id2, LABELS(node2) AS labels2, node2.value AS value2, TYPE(r) AS type";
            
    // Usecase 2: Customer calls credit agent for credit card recommendation. To get recommendation, Credit card agent : Based on recent purchases and bank offers makes a recommendation.
    public static final String GET_CARD_RECOMMENDATION =
            "MATCH (offer:Offer)-[r:HAS_OFFER]-(bank) WHERE offer.value IN ['travel'] RETURN ID(offer) AS id, LABELS(offer) AS labels, offer.value AS value, ID(bank) AS id2, LABELS(bank) AS labels2, bank.value AS value2, TYPE(r) AS type";

    // Usecase 3: Bank agent is looking customer to send credit card marketing emails.
    public static final String GET_CUSTOMER_OF_BANK =
            "MATCH (bank:Bank)-[r:HAS_ACCOUNT]-(customer:Customer) WHERE bank.value = '%s' RETURN ID(bank) AS id, LABELS(bank) AS labels, bank.value AS value, ID(customer) AS id2, LABELS(customer) AS labels2, customer.value AS value2, TYPE(r) AS type";
    // Ignore
    public static final String GET_CONNECTED_USERS =
            "MATCH (customer:Customer)-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]->(x)<-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]-(customer2:Customer) WHERE ID(customer) = '%s' RETURN id(customer) AS id, id(customer2) AS id2, COLLECT(ID(x)) AS shared";

    // Ignore
    public static final String FRAUD_RING_FOUR_CUSTOMERS =
            "MATCH (customer:Customer)-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]->(x1)<-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]-(customer2:Customer), (customer2)-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]->(x2)<-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]-(customer3:Customer), (customer3)-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]->(x3)<-[:HAS_DEVICE|HAS_EMAIL|HAS_NAME|HAS_TOKEN|HAS_PHONE|HAS_USERNAME]-(customer4:Customer) WHERE ID(customer) = '%s' AND customer <> customer3 AND customer2 <> customer4 RETURN id(customer) AS id, id(customer2) AS id2, id(customer3) AS id3, id(customer4) AS id4";


    public static List<Map<String, Object>> nodeByValue(String value) {
        String query = String.format(FIND_NODE_BY_VALUE, value);
        return Iterators.asList(NeptuneExtension.readQuery(query, new HashMap<>()));
    }

    public static List<Map<String, Object>> relatedById(String id) {
        String query = String.format(GET_RELATED_BY_ID, id);
        return Iterators.asList(NeptuneExtension.readQuery(query, new HashMap<>()));
    }
    
      public static List<Map<String, Object>> getCustomerOfBank(String id) {
        String query = String.format(GET_CUSTOMER_OF_BANK, id);
        return Iterators.asList(NeptuneExtension.readQuery(query, new HashMap<>()));
    }
    
      public static List<Map<String, Object>> getCardRecommendation(String id) {
        String query = String.format(GET_CARD_RECOMMENDATION, id);
        return Iterators.asList(NeptuneExtension.readQuery(query, new HashMap<>()));
    }

    public static List<Map<String, Object>> connected(String id) {
        String query = String.format(GET_CONNECTED_USERS, id);
        return Iterators.asList(NeptuneExtension.readQuery(query, new HashMap<>()));
    }

    public static List<Map<String, Object>> fraudRing(String id) {
        String query = String.format(FRAUD_RING_FOUR_CUSTOMERS, id);
        return Iterators.asList(NeptuneExtension.readQuery(query, new HashMap<>()));
    }
}

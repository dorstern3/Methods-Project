package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection;
import db.DBreports;
import db.DBselect;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import common.CancellationReportRow;
import common.Message;
import common.MessageType;
import common.OccupancyReportRow;
import common.Order;
import common.ParameterRequest;
import common.TotalVisitorsReportRow;

// Logic from the OCSF folder
public class EchoServer extends AbstractServer {
    
    private ServerGUI gui;
    
    private final Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();
    private final Set<String> activeTravelers = ConcurrentHashMap.newKeySet();
    
    public EchoServer(int port, ServerGUI gui) {
        super(port);
        this.gui = gui;
    }

    // method that is invoked whenever a message is received from a client and routes it to the appropriate action
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received from client: " + msg);
        try {
            if (msg instanceof Message) {
            	Message message = (Message) msg;
            	
            	switch(message.getType()) {
	            	case LOGIN_REQUEST:
	            	    handleLoginRequest(message, client);
	            	    break;
	            	case LOGOUT_REQUEST: {
	            	    String workerId = (String) message.getData();
	            	    if (workerId != null) {
	            	        loggedInUsers.remove(workerId); 
	            	        client.setInfo("workerId", null);
	            	        System.out.println("Server Success: Employee (ID: " + workerId + ") logged out orderly.");
	            	        client.sendToClient(new Message(MessageType.LOGOUT_SUCCESS, null));
	            	    }
	            	    break;
	            	}
	            	case TRAVELER_LOGIN: {
	            	    String travelerId = (String) message.getData();

	            	    if (activeTravelers.contains(travelerId)) {
	            	        client.sendToClient(new Message(MessageType.LOGIN_FAILED, "Traveler already connected"));
	            	        System.out.println("Server Warning: Traveler [" + travelerId + "] is already active.");
	            	        break;
	            	    }
            	        client.setInfo("travelerId", travelerId);
            	        activeTravelers.add(travelerId);
            	        client.sendToClient(new Message(MessageType.LOGIN_SUCCESS, null));
            	        System.out.println("Server Success: Traveler ID [" + travelerId + "] entered session.");
            	        break;
	            	}
	            	case TRAVELER_LOGOUT: {
	            	    String travelerIdentifier = (String) message.getData();
	            	    if (travelerIdentifier != null) {
	            	        activeTravelers.remove(travelerIdentifier); 
	            	        client.setInfo("travelerId", null);         
	            	        System.out.println("Server Success: Traveler [" + travelerIdentifier + "] removed from active set.");
	            	        client.sendToClient(new Message(MessageType.LOGOUT_SUCCESS, null));
	            	    }
	            	    break;
	            	}
	            	case GET_FULL_PRICE:
	                    handleGetFullPrice(message, client);
	                    break;
	                case CHECK_PROMOTIONS:
	                    handleCheckPromotions(message, client);
	                    break;
	                case VALIDATE_ORDER:
	                    handleValidateOrder(message, client);
	                    break;
	                case CHECK_CAPACITY:
	                    handleCheckCapacity(message, client);
	                    break;
	                case VERIFY_GUIDE:
	                    handleVerifyGuide(message, client);
	                    break;
	                case CONFIRM_PAYMENT:
	                    handleConfirmPayment(message, client);
	                    break;
	                case VERIFY_SUBSCRIBER:
	                    handleVerifySubscriber(message, client);
	                    break;
	                case EXIT_PARK:
	                    handleExitPark(message, client);
	                    break;
	            	case GET_PARKS:{
	            		ArrayList<String> parks = getParksFromDB();
	            		client.sendToClient(new Message(MessageType.GET_PARKS_RESPONSE, parks));
	                    break;
	            	}
	            	case GET_TOTAL_VISITOR_REPORT: {
	            		String[] params = (String[]) message.getData();
	                    ArrayList<TotalVisitorsReportRow> reportData = db.DBreports.getTotalVisitorsReportFromDB(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_TOTAL_VISITOR_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                
	                case GET_OCCUPANCY_REPORT: {
	                	String[] params = (String[]) message.getData();
	                    ArrayList<OccupancyReportRow> reportData = db.DBreports.getOccupancyReportFromDB(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_OCCUPANCY_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                
	                case GET_VISITOR_REPORT: {
	                	String[] params = (String[]) message.getData();
	                    Map<String, Double[]> reportData = db.DBreports.getVisitorsReport(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_VISITOR_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                
	                case GET_CANCELLATION_REPORT: {
	                	String[] params = (String[]) message.getData();
	                    ArrayList<CancellationReportRow> reportData = db.DBreports.getCancellationReport(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_CANCELLATION_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                case GET_PARKS_CANCELLATION_REPORT:{
	                	String[] params = (String[]) message.getData();
	                	ArrayList<CancellationReportRow> reportData = db.DBreports.getParksCancellationReport(params[0],params[1]);
	                	client.sendToClient(new Message(MessageType.GET_PARKS_CANCELLATION_REPORT_RESPONSE, reportData));
		                break;
	                }
	                case REGISTER_FAMILY_SUBSCRIBER:
	                    handleRegisterFamilySubscriber(message, client);
	                    break;
	                case REGISTER_SINGLE_SUBSCRIBER:
	                    handleRegisterSingleSubscriber(message, client);
	                    break;
	                case REGISTER_GUIDE:
	                    handleRegisterGuide(message, client);
	                    break;
	                case SUBMIT_PARAMETER_REQUEST:
	                    handleSubmitParameterRequest(message, client);
	                    break;
	                case GET_PENDING_PARAMETER_REQUESTS:
	                    handleGetPendingParameterRequests(message, client);
	                    break;
	                case UPDATE_PARAMETER_REQUEST_STATUS:
	                    handleUpdateParameterRequestStatus(message, client);
	                    break;
	                case ACTIVATE_PROMOTION:
	                    handleActivatePromotion(message, client);
	                    break;
	                case IDENTIFY_TRAVELER:{
						String travelerId = (String) message.getData();
						System.out.println("Server is now looking for traveler ID: " + travelerId);

						String result = db.DBselect.identifyTravelerInDB(travelerId);

						Message responseMsg = new Message(MessageType.IDENTIFY_TRAVELER_RESPONSE, result);
						try {
							client.sendToClient(responseMsg);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
	                }
					case CHECK_AVAILABILITY:{
						ArrayList<Object> orderDetails = (ArrayList<Object>) message.getData();

						boolean isAvailable = DBselect.checkAvailability(orderDetails);

						Message replyMsg = new Message(MessageType.CHECK_AVAILABILITY_RESULT, isAvailable);
						try {
							client.sendToClient(replyMsg);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
					case ENTER_WAITING_LIST:{
						ArrayList<Object> waitlistData = (ArrayList<Object>) ((Message) msg).getData();

						boolean isWaitlistSaved = db.UpdateOrderTable.saveToWaitingList(waitlistData);

						Message waitlistReply = new Message(MessageType.ENTER_WAITING_LIST_RESULT, isWaitlistSaved);
						try {
							client.sendToClient(waitlistReply);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
					case SAVE_NEW_ORDER:{
						ArrayList<Object> orderDataToSave = (ArrayList<Object>) message.getData();

						String generatedQR = db.UpdateOrderTable.saveNewOrder(orderDataToSave);

						if (generatedQR != null) {
							Message successMsg = new Message(MessageType.SAVE_SUCCESS, generatedQR);
							try {
								client.sendToClient(successMsg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								client.sendToClient(new Message(MessageType.ERROR, null));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					}
					case GET_ALTERNATIVE_DATES:{
						try {
							ArrayList<Object> originalOrder = (ArrayList<Object>) message.getData();

							ArrayList<String> altDatesList = db.DBselect.getAlternativeDatesList(originalOrder);

							Message altDatesReply = new Message(MessageType.GET_ALTERNATIVE_DATES_RESULT, altDatesList);
							client.sendToClient(altDatesReply);

						} catch (Exception e) {
							System.out.println("Error processing alternative dates request");
							e.printStackTrace();
						}
						break;
					}
					case FETCH_ORDER_DETAILS: {
						ArrayList<Object> searchParams = (ArrayList<Object>) message.getData();
						int orderNum = (int) searchParams.get(0);
						String travelerIdStr = (String) searchParams.get(1);

						ArrayList<Object> validatedOrderDetails = db.DBselect.fetchOrderWithValidation(orderNum,
								travelerIdStr);

						try {
							client.sendToClient(new Message(MessageType.FETCH_ORDER_RESULT, validatedOrderDetails));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					case UPDATE_ORDER_STATUS: {
						ArrayList<Object> updateData = (ArrayList<Object>) message.getData();
						int orderNumToUpdate = (int) updateData.get(0);
						String newStatus = (String) updateData.get(1);

						ArrayList<Object> updateResultList = db.UpdateOrderTable.updateOrderStatus(orderNumToUpdate,newStatus);

						try {
							client.sendToClient(new Message(MessageType.UPDATE_ORDER_RESULT, updateResultList));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					case GET_PARK_OCCUPANCY: {
						handleGetParkOccupancy((Message) msg, client);
				        break;
					}
	                default:
	                	System.out.println("Server: Unknown message type received.");
	                    break;
	            }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /**
     * Fetches the dynamic real-time occupancy and maximum capacity configuration for a specific park.
     * @param message The message containing the target park name (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleGetParkOccupancy(Message message, ConnectionToClient client) {
        String parkName = (String) message.getData();
        int[] capacityResults = new int[2]; // Index 0: current_occupancy, Index 1: max_capacity
        
        try {
            Connection conn = DBconnection.getConnection();
            String query = "SELECT current_occupancy, max_capacity FROM gonature_db_new.Parks WHERE park_name = ?";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, parkName);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                capacityResults[0] = rs.getInt("current_occupancy");
                capacityResults[1] = rs.getInt("max_capacity");
            }
            
            rs.close();
            ps.close();
            
        } catch (Exception e) {
            System.err.println("Server: Error retrieving live park occupancy metadata.");
            e.printStackTrace();
        }
        
       
        try {
            client.sendToClient(new Message(MessageType.GET_PARK_OCCUPANCY_RESPONSE, capacityResults));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes a park exit request sent from either a park employee or a visitor gate.
     * Maps identifications using the single 'id' column.
     * Updates and releases park occupancy.
     *
     * @param message The message containing an Object array: [0] OrderID/QRCode (String), [1] TravelerID (String or null).
     * @param client  The connection thread representing the specific client.
     */
    private void handleExitPark(Message message, ConnectionToClient client) {
        // Extract the data array received from the client
        Object[] data = (Object[]) message.getData();
        String inputStr = (String) data[0];   // Can be an Order Number or a scanned QR Code
        String travelerId = (String) data[1]; // Traveler ID/Subscriber Number, or null if performed by an employee
        
        boolean success = false;

        try {
            Connection conn = DBconnection.getConnection();
            String selectQuery;
            PreparedStatement psSelect;

            // Step 1: Formulate the query based on the single id column
            if (travelerId == null) {
                // Employee Mode: Bypass traveler ID cross-referencing
                selectQuery = "SELECT order_number, number_of_visitors, park_name FROM gonature_db_new.`Order` " +
                              "WHERE (order_number = ? OR QR_code = ?) AND status = 'Entered'";
                psSelect = conn.prepareStatement(selectQuery);
                psSelect.setString(1, inputStr);
                psSelect.setString(2, inputStr);
            } else {
                // Visitor Mode: Cross-reference directly with the single id field
                selectQuery = "SELECT order_number, number_of_visitors, park_name FROM gonature_db_new.`Order` " +
                              "WHERE (order_number = ? OR QR_code = ?) AND id = ? AND status = 'Entered'";
                psSelect = conn.prepareStatement(selectQuery);
                psSelect.setString(1, inputStr);
                psSelect.setString(2, inputStr);
                psSelect.setString(3, travelerId); // Verified against the single identity column context
            }
            
            ResultSet rs = psSelect.executeQuery();

            // Step 2: If a matching active record is found, proceed with the exit workflow
            if (rs.next()) {
                String actualOrderNumber = rs.getString("order_number"); 
                int visitorsAmount = rs.getInt("number_of_visitors");
                String parkName = rs.getString("park_name");

                // Step 3: Update departure time
                String updateOrder = "UPDATE gonature_db_new.`Order` SET exit_time = CURTIME() WHERE order_number = ?";
                PreparedStatement psUpdateOrder = conn.prepareStatement(updateOrder);
                psUpdateOrder.setString(1, actualOrderNumber);
                psUpdateOrder.executeUpdate();
                psUpdateOrder.close();

                // Step 4: Decrement the park's current occupancy to free up capacity
                String updatePark = "UPDATE gonature_db_new.Parks SET current_occupancy = current_occupancy - ? WHERE park_name = ?";
                PreparedStatement psUpdatePark = conn.prepareStatement(updatePark);
                psUpdatePark.setInt(1, visitorsAmount);
                psUpdatePark.setString(2, parkName);
                psUpdatePark.executeUpdate();
                psUpdatePark.close();

                success = true;
                System.out.println("Server: Exit registered successfully for Order: " + actualOrderNumber);
            } else {
                System.out.println("Server: Exit rejected. Parameters do not match any active 'Entered' order.");
            }
            
            rs.close();
            psSelect.close();

        } catch (Exception e) {
            System.err.println("Server: Database error during exit registration execution.");
            e.printStackTrace();
        }

        // Step 5: Dispatch the evaluation response back to the client
        try { 
            client.sendToClient(new Message(MessageType.EXIT_PARK_RESPONSE, success)); 
        } catch (Exception e) { 
            System.err.println("Server: Critical error transmitting response to client.");
            e.printStackTrace(); 
        }
    }
       
    /**
     * Fetches the base full ticket price for a specified park from the database.
     * If the park is not found or a database error occurs, a fallback price of 50.0 is used.
     * * @param message The network message containing the requested park name (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleGetFullPrice(Message message, ConnectionToClient client) {
        String requestedPark = (String) message.getData();
        double fullPrice = 50.0; // Default fallback price
        
        try {
            String query = "SELECT full_price FROM gonature_db_new.Parks WHERE park_name = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, requestedPark);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Success: Found the price in the database
                fullPrice = rs.getDouble("full_price");
                System.out.println("Server: Fetched full price (" + fullPrice + ") for park: " + requestedPark);
            } else {
                // Edge Case 1: Park not found in the database
                System.out.println("Server: WARNING - Park '" + requestedPark + "' not found. Using Fallback price: " + fullPrice);
            }
            
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            // Edge Case 2: Database connection error or query failure
            System.err.println("Server: ERROR - Database error during fetchFullPrice. Using Fallback price: " + fullPrice);
            e.printStackTrace();
        }
        
        // Send the result (either DB price or fallback) back to the client
        try { 
            client.sendToClient(new Message(MessageType.GET_FULL_PRICE_RESPONSE, fullPrice)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Retrieves any active promotional discount values for a specified park.
     * * @param message The network message containing the target park name (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleCheckPromotions(Message message, ConnectionToClient client) {
        String parkForPromo = (String) message.getData();
        double discount = 0.0; // Default no discount
        
        try {
            String query = "SELECT additonal_discount FROM gonature_db_new.Parks WHERE park_name = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, parkForPromo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                discount = rs.getDouble("additonal_discount");
                System.out.println("Server: Found discount of " + discount + " for park: " + parkForPromo);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Database error during checkActivePromotions.");
            e.printStackTrace();
        }
        
        try { client.sendToClient(new Message(MessageType.CHECK_PROMOTIONS_RESPONSE, discount)); } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Validates a pre-booked order attempting to enter the park.
     * Cross-references the provided Order ID or QR Code against the database to enforce 
     * date validity, time frame constraints, and current confirmation status.
     * * @param message The network message containing the Order ID or QR Code (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleValidateOrder(Message message, ConnectionToClient client) {
 	    String inputIdStr = (String) message.getData();
 	    
 	    try {
 	        int parsedId = Integer.parseInt(inputIdStr);
 	        
 	        // Fetch order by ID or QR to identify the specific error
 	        String query = "SELECT number_of_visitors, type_of_visitor, order_date, entry_time, status " +
 	                       "FROM gonature_db_new.`Order` " +
 	                       "WHERE (order_number = ? OR QR_code = ?) AND exit_time IS NULL";
 	                       
 	        Connection conn = DBconnection.getConnection();
 	        PreparedStatement pstmt = conn.prepareStatement(query);
 	        pstmt.setInt(1, parsedId); 
 	        pstmt.setInt(2, parsedId); 
 	        ResultSet rs = pstmt.executeQuery();
 	        
 	        if (rs.next()) {
 	            String status = rs.getString("status");
 	            java.sql.Date orderDate = rs.getDate("order_date");
 	            java.sql.Time entryTime = rs.getTime("entry_time");
 	            
 	            // Error Check 1: Check if the order is scheduled for today
 	            java.time.LocalDate today = java.time.LocalDate.now();
 	            if (!orderDate.toLocalDate().equals(today)) {
 	                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "WRONG_DATE"));
 	                return;
 	            }

 	            // Error Check 2: Check time difference
 	            java.time.LocalTime now = java.time.LocalTime.now();
 	            java.time.LocalTime scheduledTime = entryTime.toLocalTime();
 	            long minutesDifference = java.time.temporal.ChronoUnit.MINUTES.between(scheduledTime, now);
 	            
 	            if (minutesDifference > 60) {
 	                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "TIME_PASSED"));
 	                return;
 	            } else if (minutesDifference < -60) {
 	                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "TOO_EARLY"));
 	                return;
 	            }

 	            // Error Check 3: Check if the order is Confirmed
 	            if (!status.equals("Confirmed")) {
 	                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "NOT_CONFIRMED"));
 	                return;
 	            }
 	            
 	            // Success: All conditions met
 	            java.util.ArrayList<Object> orderDetails = new java.util.ArrayList<>();
 	            orderDetails.add(rs.getInt("number_of_visitors")); 
 	            orderDetails.add(rs.getString("type_of_visitor"));   
 	            
 	            client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, orderDetails));
 	            
 	        } else {
 	            // Error Check 4: The order number or QR code does not exist
 	            client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "NOT_FOUND"));
 	        }
 	        
 	        rs.close();
 	        pstmt.close();
 	    } catch (NumberFormatException e) {
 	        try { client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "INVALID_FORMAT")); } catch (Exception ex) {}
 	    } catch (Exception e) {
 	        e.printStackTrace();
 	    }
 	}

    /**
     * Checks if a specified park has enough available capacity to admit a group of casual visitors.
     * Accounts for the maximum capacity limit minus the designated casual gap.
     * * @param message The network message containing an ArrayList with: [0] amount (int), [1] parkName (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleCheckCapacity(Message message, ConnectionToClient client) {
        // 1. Extract the list from the message
        java.util.ArrayList<Object> dataList = (java.util.ArrayList<Object>) message.getData();
        
        // 2. Safely unpack the data from the list
        int requestedAmount = (int) dataList.get(0);
        String parkName = (String) dataList.get(1); // Now using the dynamic park name!
        
        boolean hasSpace = false;
        
        try {
            String query = "SELECT max_capacity, casual_gap, current_occupancy FROM gonature_db_new.Parks WHERE park_name = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, parkName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int maxCapacity = rs.getInt("max_capacity");
                int casualGap = rs.getInt("casual_gap");
                int currentOccupancy = rs.getInt("current_occupancy");
                
                int allowedCapacity = maxCapacity - casualGap;
                
                if ((currentOccupancy + requestedAmount) <= allowedCapacity) {
                    hasSpace = true;
                    System.out.println("Server: Space available for " + requestedAmount + " in " + parkName);
                } else {
                    System.out.println("Server: Park " + parkName + " is full for casual visitors.");
                }
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Database error during capacity check.");
            e.printStackTrace();
        }
        
        try { 
            client.sendToClient(new Message(MessageType.CHECK_CAPACITY_RESPONSE, hasSpace)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Verifies if a provided ID belongs to a certified group guide registered in the system.
     * * @param message The network message containing the Guide ID (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleVerifyGuide(Message message, ConnectionToClient client) {
        String guideIdStr = (String) message.getData();
        boolean isCertified = false;
        
        try {
            int guideId = Integer.parseInt(guideIdStr);
            String query = "SELECT * FROM gonature_db_new.Guide WHERE guide_id = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, guideId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                isCertified = true; 
                System.out.println("Server: Guide " + guideId + " verified successfully.");
            } else {
                System.out.println("Server: Guide verification failed for ID: " + guideId);
            }
            rs.close();
            pstmt.close();
        } catch (NumberFormatException e) {
            System.err.println("Server: Invalid Guide ID format.");
        } catch (Exception e) {
            System.err.println("Server: Database error during guide verification.");
        }
        
        try { client.sendToClient(new Message(MessageType.VERIFY_GUIDE_RESPONSE, isCertified)); } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Verifies if a provided subscriber number exists and is valid in the system.
     * * @param message The network message containing the Subscriber ID/Number (String).
     * @param client  The connection thread representing the client making the request.
     */
    private void handleVerifySubscriber(Message message, ConnectionToClient client) {
        String subIdStr = (String) message.getData();
        boolean isSubValid = false;
        
        try {
            int subId = Integer.parseInt(subIdStr);
            String query = "SELECT * FROM gonature_db_new.Subscriber WHERE sub_number = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, subId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                isSubValid = true; 
                System.out.println("Server: Subscriber " + subId + " verified successfully.");
            } else {
                System.out.println("Server: Subscriber verification failed for ID: " + subId);
            }
            rs.close();
            pstmt.close();
        } catch (NumberFormatException e) {
            System.err.println("Server: Invalid Subscriber ID format.");
        } catch (Exception e) {
            System.err.println("Server: Database error during subscriber verification.");
            e.printStackTrace();
        }
        
        try { client.sendToClient(new Message(MessageType.VERIFY_SUBSCRIBER_RESPONSE, isSubValid)); } 
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Processes transaction executions for visitor admissions inside the secure server framework.
     * Increments designated park capacities dynamically and structures order history data logs.
     * Evaluates casual vs pre-booked states, routing the client identification index to either the 
     * 'id' column or 'sub_number' column based on business rules.
     *
     * @param message The message containing the transaction details.
     * @param client  The specific client communication connection execution thread reference.
     */
    private void handleConfirmPayment(Message message, ConnectionToClient client) {
        // Disassemble the packaged transaction array structure mapped by the logic layer
        ArrayList<Object> paymentData = (ArrayList<Object>) message.getData();
        int amountToAdd = (int) paymentData.get(0);
        String orderToUpdate = (String) paymentData.get(1); 
        String parkToUpdate = (String) paymentData.get(2);
        String visitorType = (String) paymentData.get(3); 
        String visitorId = (String) paymentData.get(4); // Extracted traveler verification identification parameter
        
        String resultOrderId = null;

        try {
            Connection conn = DBconnection.getConnection();
            
            // Step 1: Dynamically increment the specific park real-time occupancy monitoring schema values
            String updatePark = "UPDATE gonature_db_new.Parks SET current_occupancy = current_occupancy + ? WHERE park_name = ?";
            PreparedStatement psPark = conn.prepareStatement(updatePark);
            psPark.setInt(1, amountToAdd);
            psPark.setString(2, parkToUpdate);
            psPark.executeUpdate();
            psPark.close();

            // Step 2: Evaluate scenario properties to manage structural Order table data modifications
            if (orderToUpdate != null && !orderToUpdate.isEmpty()) {
                // SCENARIO A: Pre-booked Order configuration (Modify existing status variables)
                int oId = Integer.parseInt(orderToUpdate);
                String updateOrder = "UPDATE gonature_db_new.`Order` SET status = 'Entered' WHERE order_number = ?";
                PreparedStatement psOrder = conn.prepareStatement(updateOrder);
                psOrder.setInt(1, oId);
                psOrder.executeUpdate();
                psOrder.close();
                
                // Return the existing order tracker reference directly back to the client
                resultOrderId = orderToUpdate;
                
            } else {
                // SCENARIO B: Casual Visitor (Insert New Order Row with a single ID column)
                // We store all identification types (Regular ID, Subscriber, Guide) in the same column
                String insertOrder = "INSERT INTO gonature_db_new.`Order` " +
                                     "(order_date, number_of_visitors, date_of_placing_order, entry_time, status, type_of_visitor, park_name, id) " +
                                     "VALUES (CURDATE(), ?, CURDATE(), CURTIME(), 'Entered', ?, ?, ?)";
                
                PreparedStatement psInsert = conn.prepareStatement(insertOrder, java.sql.Statement.RETURN_GENERATED_KEYS);
                psInsert.setInt(1, amountToAdd);
                psInsert.setString(2, visitorType);
                psInsert.setString(3, parkToUpdate); 
                psInsert.setString(4, visitorId); // Stores Regular ID / Subscriber Number / Guide ID dynamically
                
                psInsert.executeUpdate();
                
                // Extract generated auto-increment key
                java.sql.ResultSet rsKeys = psInsert.getGeneratedKeys();
                if (rsKeys.next()) {
                    resultOrderId = String.valueOf(rsKeys.getInt(1));
                }
                rsKeys.close();
                psInsert.close();
                
                System.out.println("Server: Casual entry registered. Type: " + visitorType + ", ID: " + visitorId + " -> Assigned Order: " + resultOrderId);
            }
            
        } catch (Exception e) {
            System.err.println("Server: Error during payment confirmation and database transaction persistence routine.");
            e.printStackTrace();
            resultOrderId = null; // Enforce null state resolution configuration mapping outputs
        }
        
        // Step 5: Dispatch the data resolution tracking index string parameter configuration to the client
        try { 
            client.sendToClient(new Message(MessageType.CONFIRM_PAYMENT_RESPONSE, resultOrderId)); 
        } catch (Exception e) { 
            System.err.println("Server: Fatal exception transmitting structural confirmation payload.");
            e.printStackTrace(); 
        }
    }

    
    /* ----------------------------------------------------------------------------------------------------  */   

    
    /**
     * Fetches the names of all registered parks from the database.
     */
    private ArrayList<String> getParksFromDB(){
    	ArrayList<String> parks = new ArrayList<>();
    	String query = "SELECT park_name FROM parks";
    	try (Connection conn = DBconnection.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
    		try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parks.add(rs.getString("park_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return parks;
    }
    
    
 /* ----------------------------------------------------------------------------------------------------  */   
    
    
    private void handleRegisterFamilySubscriber(Message message, ConnectionToClient client) {
        Object[] params = (Object[]) message.getData();
        int id = (int) params[0];
        String fname = (String) params[1];
        String lname = (String) params[2];
        String email = (String) params[3];
        String phone = (String) params[4];
        int familyMembers = (int) params[5];

        int generatedSubNum = new java.util.Random().nextInt(9000) + 1000;
        boolean success = false;

        String query = "INSERT INTO gonature_db_new.subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, fname);
            pstmt.setString(3, lname);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setNull(6, java.sql.Types.VARCHAR);
            pstmt.setInt(7, familyMembers);
            pstmt.setInt(8, generatedSubNum);

            success = pstmt.executeUpdate() > 0;
            if(success) {
                System.out.println("Server: Family subscriber registered successfully. Sub Number: " + generatedSubNum);
            }
        } catch (Exception e) {
            System.err.println("Server: Database error during family registration.");
            e.printStackTrace();
        }

        try {
            Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, generatedSubNum) 
                                       : new Message(MessageType.REGISTRATION_FAILED, null);
            client.sendToClient(response);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleRegisterSingleSubscriber(Message message, ConnectionToClient client) {
        Object[] params = (Object[]) message.getData();
        int id = (int) params[0];
        String fname = (String) params[1];
        String lname = (String) params[2];
        String email = (String) params[3];
        String phone = (String) params[4];
        int familyMembers = (int) params[5];

        int generatedSubNum = new java.util.Random().nextInt(9000) + 1000;
        boolean success = false;

        String query = "INSERT INTO gonature_db_new.subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, fname);
            pstmt.setString(3, lname);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setNull(6, java.sql.Types.VARCHAR);
            pstmt.setInt(7, familyMembers);
            pstmt.setInt(8, generatedSubNum);

            success = pstmt.executeUpdate() > 0;
            if(success) {
                System.out.println("Server: Single subscriber registered successfully. Sub Number: " + generatedSubNum);
            }
        } catch (Exception e) {
            System.err.println("Server: Database error during single registration.");
            e.printStackTrace();
        }

        try {
            Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, generatedSubNum) 
                                       : new Message(MessageType.REGISTRATION_FAILED, null);
            client.sendToClient(response);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleRegisterGuide(Message message, ConnectionToClient client) {
        Object[] params = (Object[]) message.getData();
        String fname = (String) params[0];
        String lname = (String) params[1];
        String email = (String) params[2];
        String phone = (String) params[3];
        boolean success = false;

        String query = "INSERT INTO gonature_db_new.Guide (fname, lname, email, phone_number) VALUES (?, ?, ?, ?);";
        
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);

            success = pstmt.executeUpdate() > 0;
            if(success) {
                System.out.println("Server: Group Guide registered successfully: " + fname + " " + lname);
            }
        } catch (Exception e) {
            System.err.println("Server: Database error during guide registration.");
            e.printStackTrace();
        }

        try {
            Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, null) 
                                       : new Message(MessageType.REGISTRATION_FAILED, null);
            client.sendToClient(response);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void handleSubmitParameterRequest(Message message, ConnectionToClient client) {
        ParameterRequest req = (ParameterRequest) message.getData();
        boolean success = false;
        
        String query = "INSERT INTO gonature_db_new.parameter_requests (park_name, worker_id, parameter_name, current_value, request_value, status) VALUES (?, ?, ?, ?, ?, 'Pending');";
        
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, req.getParkName());
            pstmt.setInt(2, req.getWorkerId());
            pstmt.setString(3, req.getParameterName());
            pstmt.setInt(4, req.getCurrentValue());
            pstmt.setInt(5, req.getRequestedValue());
            
            success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("Server: New parameter request inserted for park: " + req.getParkName());
            }
        } catch (Exception e) {
            System.err.println("Server: Database error during parameter request submission.");
            e.printStackTrace();
        }

        try {
            client.sendToClient(success ? new Message(MessageType.REQUEST_SUBMIT_SUCCESS, null) 
                                       : new Message(MessageType.REQUEST_SUBMIT_FAILED, null));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleGetPendingParameterRequests(Message message, ConnectionToClient client) {
        ArrayList<ParameterRequest> pendingList = new ArrayList<>();
        String query = "SELECT request_id, park_name, worker_id, parameter_name, current_value, request_value, status, request_date FROM gonature_db_new.parameter_requests WHERE status = 'Pending';";

        try (Connection conn = DBconnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query); 
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                pendingList.add(new ParameterRequest(
                    rs.getInt("request_id"),
                    rs.getString("park_name"),
                    rs.getInt("worker_id"),
                    rs.getString("parameter_name"),
                    rs.getInt("current_value"),
                    rs.getInt("request_value"),
                    rs.getString("status"),
                    rs.getTimestamp("request_date")
                ));
            }
            System.out.println("Server: Fetched " + pendingList.size() + " pending parameter requests from DB.");
        } catch (Exception e) {
            System.err.println("Server: Database error during fetching pending requests.");
            e.printStackTrace();
        }

        try {
            client.sendToClient(new Message(MessageType.GET_PENDING_REQUESTS_RESPONSE, pendingList));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleUpdateParameterRequestStatus(Message message, ConnectionToClient client) {
        ParameterRequest req = (ParameterRequest) message.getData();
        boolean success = false;
        Connection conn = null;

        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);

            String updateReqSql = "UPDATE gonature_db_new.parameter_requests SET status = ? WHERE request_id = ?;";
            try (PreparedStatement updateReqStmt = conn.prepareStatement(updateReqSql)) {
                updateReqStmt.setString(1, req.getStatus());
                updateReqStmt.setInt(2, req.getRequestId());
                updateReqStmt.executeUpdate();
            }

            if ("Approved".equals(req.getStatus())) {
                String updateParkSql = "UPDATE gonature_db_new.Parks SET " + req.getParameterName() + " = ? WHERE park_name = ?;";
                try (PreparedStatement updateParkStmt = conn.prepareStatement(updateParkSql)) {
                    updateParkStmt.setInt(1, req.getRequestedValue());
                    updateParkStmt.setString(2, req.getParkName());
                    updateParkStmt.executeUpdate();
                }
            }

            conn.commit(); 
            conn.setAutoCommit(true);
            success = true;
            System.out.println("Server: Transaction completed. Request ID " + req.getRequestId() + " updated to: " + req.getStatus());
            
        } catch (Exception ex) {
            System.err.println("Server: Error during request status update transaction. Rolling back...");
            try { if (conn != null) conn.rollback(); } catch (Exception se) { se.printStackTrace(); }
            ex.printStackTrace();
        }

        try {
            client.sendToClient(success ? new Message(MessageType.UPDATE_REQUEST_SUCCESS, null) 
                                       : new Message(MessageType.UPDATE_REQUEST_FAILED, null));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleActivatePromotion(Message message, ConnectionToClient client) {
        Object[] params = (Object[]) message.getData();
        String parkName = (String) params[0];
        double dbDiscountValue = (double) params[1];
        boolean success = false;

        String query = "UPDATE gonature_db_new.Parks SET additonal_discount = ? WHERE park_name = ?;";
        
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, dbDiscountValue);
            pstmt.setString(2, parkName);

            success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("Server: Activated promotion discount (" + dbDiscountValue + ") for park: " + parkName);
            }
        } catch (Exception e) {
            System.err.println("Server: Database error during promotion activation.");
            e.printStackTrace();
        }

        try {
            client.sendToClient(success ? new Message(MessageType.PROMOTION_ACTIVATED_SUCCESS, null) 
                                       : new Message(MessageType.PROMOTION_ACTIVATED_FAILED, null));
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /* ----------------------------------------------------------------------------------------------------  */  
    
    /**
     * Handles incoming login requests from clients.
     * Validates credentials against the database and returns employee data if successful.
     */
    private void handleLoginRequest(Message message, ConnectionToClient client) {
        Object[] credentials = (Object[]) message.getData();
        String workerName = (String) credentials[0];
        String hashedPassword = (String) credentials[1];
        
        Object[] workerData = null;
        boolean isSuccess = false;

        String query = "SELECT worker_id, fname, lname, email, role, park_name " +
                       "FROM gonature_db_new.Workers WHERE fname = ? AND hash_password = ?;";

        try (Connection conn = DBconnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
        	//pstmt.setInt(1, Integer.parseInt(workerId));
        	pstmt.setString(1,workerName);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isSuccess = true;
                    workerData = new Object[] {
                        rs.getInt("worker_id"),
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("park_name") 
                    };
                }
            }
        } catch (Exception e) {
            System.err.println("Server Error: Database failure during login for user " + workerName);
            e.printStackTrace();
        }
        try {
            if (isSuccess) {
            	String workerId = String.valueOf(workerData[0]);
            	if(loggedInUsers.contains(workerId)) {
            		client.sendToClient(new Message(MessageType.LOGIN_FAILED, "User already Logged in"));
            		System.out.println("Server Warning: Rejected login for '" + workerName + "' (ID: " + workerId + ") - User already logged in.");
            		return;
            	}
            	client.setInfo("workerId", workerId);
                loggedInUsers.add(workerId);
                
                client.sendToClient(new Message(MessageType.LOGIN_SUCCESS, workerData));
                System.out.println("Server Success: Employee '" + workerName + "' authenticated successfully.");
            } else {
                client.sendToClient(new Message(MessageType.LOGIN_FAILED, "Incorrect username or password."));
                System.out.println("Server Warning: Failed login attempt for username: " + workerName);
            }
        } catch (Exception e) {
            System.err.println("Server Error: Failed to send login response to client.");
            e.printStackTrace();
        }
    }
    /* ----------------------------------------------------------------------------------------------------  */   
    //Action performed as soon as a new customer connects: saving their details and updating the GUI
    @Override
    protected void clientConnected(ConnectionToClient client) {
        String ip = client.getInetAddress().getHostAddress().replace("/", "");
        String hostName = client.getInetAddress().getHostName();
        
        // Truly unique port extraction for each connection
        String info = client.toString();
        String port = info.substring(info.lastIndexOf(" ") + 1).replaceAll("[^0-9]", "");

        client.setInfo("ip", ip);
        client.setInfo("host", hostName);
        client.setInfo("port", port);

        System.out.println("Client connected! IP: " + ip + " Port: " + port);
        if (gui != null) {
            gui.updateClientDetails(ip, hostName, "Connected", port);
        }
    }

    // Handling client disconnection when it disconnects in an orderly manner
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        handleDisconnection(client);
    }

    //Handling client disconnection in the event of an error or forced disconnection
    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        handleDisconnection(client);
    }

    // Printing to the terminal and updating the graphical interface to remove the customer from the list
    private void handleDisconnection(ConnectionToClient client) {
        String ip = (String) client.getInfo("ip");
        String host = (String) client.getInfo("host");
        String port = (String) client.getInfo("port");
        
        String workerId = (String) client.getInfo("workerId");
        if (workerId != null) {
            loggedInUsers.remove(workerId);
            System.out.println("Cleaned workerId [" + workerId + "] from loggedInUsers Set.");
        }
        
        String travelerId = (String) client.getInfo("travelerId");
        if (travelerId != null) {
            activeTravelers.remove(travelerId); 
            System.out.println("Cleaned travelerId [" + travelerId + "] from activeTravelers Set.");
        }
        
        if (ip != null && port != null) {
            System.out.println("Client disconnected! IP: " + ip + " Port: " + port);
            if (gui != null) {
                gui.updateClientDetails(ip, host, "Disconnected", port);
            }
        }
    }

    // Notification as soon as the server starts listening for connections on the specified port
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }
}
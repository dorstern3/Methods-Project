package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection;
import db.DBcustomerService;
import db.DBmanagers;
import db.DBparks;
import db.DBselect;

import java.sql.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
import db.DBSimulation;

/**
 * Main server implementation extending AbstractServer. Handles client
 * communication and routes requests to database services.
 */
public class EchoServer extends AbstractServer {

	private ServerGUI gui;

	private final Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();
	private final Set<String> activeTravelers = ConcurrentHashMap.newKeySet();

	/**
	 * Initializes the server on the specified port.
	 * 
	 * @param port The port number.
	 * @param gui  The UI interface for logging server events.
	 */
	public EchoServer(int port, ServerGUI gui) {
		super(port);
		this.gui = gui;
	}

	/**
	 * Routes incoming client messages to the appropriate handling methods based on
	 * message type.
	 * 
	 * @param msg    The received message.
	 * @param client The connection instance of the client.
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received from client: " + msg);
		try {
			if (msg instanceof Message) {
				Message message = (Message) msg;

				switch (message.getType()) {
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
					if(travelerId.length() == 4) {
						String subscriberId = db.DBselect.getSubscriberId(travelerId);
						if(subscriberId == null) {
							client.sendToClient(new Message(MessageType.LOGIN_FAILED , "There is no subscriber with " + travelerId + " subscriber number"));
							break;
						}
						travelerId = subscriberId;
					}
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
						System.out.println(
								"Server Success: Traveler [" + travelerIdentifier + "] removed from active set.");
						client.sendToClient(new Message(MessageType.LOGOUT_SUCCESS, null));
					}
					break;
				}
				case GET_FULL_PRICE:
					DBparks.handleGetFullPrice(message, client);
					break;
				case CHECK_PROMOTIONS:
					DBparks.handleCheckPromotions(message, client);
					break;
				case VALIDATE_ORDER:
					DBparks.handleValidateOrder(message, client);
					break;
				case CHECK_CAPACITY:
					DBparks.handleCheckCapacity(message, client);
					break;
				case VERIFY_GUIDE:
					DBparks.handleVerifyGuide(message, client);
					break;
				case CONFIRM_PAYMENT:
					DBparks.handleConfirmPayment(message, client);
					break;
				case VERIFY_SUBSCRIBER:
					DBparks.handleVerifySubscriber(message, client);
					break;
				case GET_SUBSCRIBER_DETAILS:
				    handleGetSubscriberDetails(message, client);
				    break;
				case EXIT_PARK:
					DBparks.handleExitPark(message, client);
					break;
				case GET_PARKS: {
					ArrayList<String> parks = DBparks.getParksFromDB();
					client.sendToClient(new Message(MessageType.GET_PARKS_RESPONSE, parks));
					break;
				}
				case GET_TOTAL_VISITOR_REPORT: {
					String[] params = (String[]) message.getData();
					ArrayList<TotalVisitorsReportRow> reportData = db.DBreports.getTotalVisitorsReportFromDB(params[0],
							params[1], params[2]);
					client.sendToClient(new Message(MessageType.GET_TOTAL_VISITOR_REPORT_RESPONSE, reportData));
					break;
				}

				case GET_OCCUPANCY_REPORT: {
					String[] params = (String[]) message.getData();
					ArrayList<OccupancyReportRow> reportData = db.DBreports.getOccupancyReportFromDB(params[0],
							params[1], params[2]);
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
					ArrayList<CancellationReportRow> reportData = db.DBreports.getCancellationReport(params[0],
							params[1], params[2]);
					client.sendToClient(new Message(MessageType.GET_CANCELLATION_REPORT_RESPONSE, reportData));
					break;
				}
				case GET_PARKS_CANCELLATION_REPORT: {
					String[] params = (String[]) message.getData();
					ArrayList<CancellationReportRow> reportData = db.DBreports.getParksCancellationReport(params[0],
							params[1]);
					client.sendToClient(new Message(MessageType.GET_PARKS_CANCELLATION_REPORT_RESPONSE, reportData));
					break;
				}
				case REGISTER_FAMILY_SUBSCRIBER:
					DBcustomerService.handleRegisterFamilySubscriber(message, client);
					break;
				case REGISTER_SINGLE_SUBSCRIBER:
					DBcustomerService.handleRegisterSingleSubscriber(message, client);
					break;
				case REGISTER_GUIDE:
					DBcustomerService.handleRegisterGuide(message, client);
					break;
				case SUBMIT_PARAMETER_REQUEST:
					DBmanagers.handleSubmitParameterRequest(message, client);
					break;
				case GET_PENDING_PARAMETER_REQUESTS:
					DBmanagers.handleGetPendingParameterRequests(message, client);
					break;
				case UPDATE_PARAMETER_REQUEST_STATUS:
					DBmanagers.handleUpdateParameterRequestStatus(message, client);
					break;
				case ACTIVATE_PROMOTION:
					DBmanagers.handleActivatePromotion(message, client);
					break;
				case IDENTIFY_TRAVELER: {
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
				case CHECK_ORDER_EXISTENCE: {
				    String inputId = (String) message.getData();
				    try {
				        String travelerId = inputId;

				        if (inputId != null && inputId.length() == 4) {
				            travelerId = db.DBselect.getTravelerIdBySubNumber(inputId);
				            if (travelerId == null) {
				                client.sendToClient(new Message(MessageType.CHECK_ORDER_RESPONSE, false));
				                break;
				            }
				        }

				        boolean exists = db.DBselect.hasActiveOrder(travelerId);
				        client.sendToClient(new Message(MessageType.CHECK_ORDER_RESPONSE, exists));
				        
				    } catch (Exception e) {
				        System.err.println("Server Error during CHECK_ORDER_EXISTENCE: " + e.getMessage());
				        e.printStackTrace();
				        client.sendToClient(new Message(MessageType.ERROR, "Server error checking order."));
				    }
				    break;
				}
				case CHECK_AVAILABILITY: {
					Order orderDetails = (Order) message.getData();
					boolean isAvailable = DBselect.checkAvailability(orderDetails);

					Message replyMsg = new Message(MessageType.CHECK_AVAILABILITY_RESULT, isAvailable);
					try {
						client.sendToClient(replyMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				case ENTER_WAITING_LIST: {
					Order waitlistData = (Order) message.getData();
					boolean isWaitlistSaved = db.UpdateOrderTable.saveToWaitingList(waitlistData);

					Message waitlistReply = new Message(MessageType.ENTER_WAITING_LIST_RESULT, isWaitlistSaved);
					try {
						client.sendToClient(waitlistReply);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				case SAVE_NEW_ORDER: {
					Order orderDataToSave = (Order) message.getData();
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
				case GET_ALTERNATIVE_DATES: {
					try {
						Order originalOrder = (Order) message.getData();
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

				    try {
				        if (travelerIdStr != null && travelerIdStr.length() == 4) {
				            String convertedId = db.DBselect.getTravelerIdBySubNumber(travelerIdStr);
				            if (convertedId != null) {
				                travelerIdStr = convertedId;
				            }
				        }

				        Order validatedOrder = db.DBselect.fetchOrderWithValidation(orderNum, travelerIdStr);
				        client.sendToClient(new Message(MessageType.FETCH_ORDER_RESULT, validatedOrder));
				        
				    } catch (IOException e) {
				        System.err.println("Server Error during FETCH_ORDER_DETAILS: " + e.getMessage());
				        e.printStackTrace();
				    }
				    break;
				}
				case UPDATE_ORDER_STATUS: {
					ArrayList<Object> updateData = (ArrayList<Object>) message.getData();
					int orderNumToUpdate = (int) updateData.get(0);
					String newStatus = (String) updateData.get(1);

					ArrayList<Object> updateResultList = db.UpdateOrderTable.updateOrderStatus(orderNumToUpdate,
							newStatus);

					try {
						client.sendToClient(new Message(MessageType.UPDATE_ORDER_RESULT, updateResultList));
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				case GET_PARK_OCCUPANCY: {
					DBparks.handleGetParkOccupancy((Message) msg, client);
					break;
				}
				case SIMULATE_24H_REMINDER: {
					ArrayList<Order> orders = db.DBSimulation.getPendingRemindersForTomorrow();

					try {
						client.sendToClient(new Message(MessageType.SIMULATE_24H_REMINDER_RESPONSE, orders));
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				case SIMULATE_WAITLIST_TIMEOUT: {
					ArrayList<String> messages = db.DBSimulation.handleWaitlistTimeouts();
					try {
						client.sendToClient(new Message(MessageType.SIMULATE_WAITLIST_RESPONSE, messages));
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}

				case SIMULATE_CONFIRMATION_TIMEOUT: {
					ArrayList<String> messages = db.DBSimulation.handleConfirmationTimeouts();
					try {
						client.sendToClient(new Message(MessageType.SIMULATE_CONFIRMATION_RESPONSE, messages));
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				case CLEAN_WAITING_LIST: {
					int canceledCount = db.UpdateOrderTable.cleanWaitingListForToday();
					client.sendToClient(new Message(MessageType.CLEAN_WAITING_LIST_RESULT, canceledCount));
					break;
				}
				case UPDATE_SUBSCRIBER_DETAILS:{
				    handleUpdateSubscriberDetails(message, client);
				    break;
				}
				case GET_SUBSCRIBERS_LIST:{
				    db.DBparks.handleGetSubscribersList(message, client);
				    break;
				}
				case GET_WORKERS_LIST:{
					db.DBparks.handleGetWorkersList(message, client);
				    break;
				}
				default:
					System.out.println("Server: Unknown message type received.");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Handles incoming login requests from employees. Validates credentials against
	 * the database and returns employee data if successful.
	 */
	private void handleLoginRequest(Message message, ConnectionToClient client) {
		Object[] credentials = (Object[]) message.getData();
		String workerName = (String) credentials[0];
		String password = (String) credentials[1];

		Object[] workerData = null;
		boolean isSuccess = false;

		String query = "SELECT worker_id, fname, lname, email, role, park_name "
				+ "FROM gonature_db_new.Workers WHERE fname = ? AND hash_password = ?;";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, workerName);
			pstmt.setString(2, password);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					isSuccess = true;
					workerData = new Object[] { rs.getInt("worker_id"), rs.getString("fname"), rs.getString("lname"),
							rs.getString("email"), rs.getString("role"), rs.getString("park_name") };
				}
			}
		} catch (Exception e) {
			System.err.println("Server Error: Database failure during login for user " + workerName);
			e.printStackTrace();
		}
		try {
			if (isSuccess) {
				String workerId = String.valueOf(workerData[0]);
				if (loggedInUsers.contains(workerId)) {
					client.sendToClient(new Message(MessageType.LOGIN_FAILED, "User already Logged in"));
					System.out.println("Server Warning: Rejected login for '" + workerName + "' (ID: " + workerId
							+ ") - User already logged in.");
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

	/*
	 * -----------------------------------------------------------------------------
	 * -----------------------
	 */
	// Action performed as soon as a new customer connects: saving their details and
	// updating the GUI
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

	// Handling client disconnection in the event of an error or forced
	// disconnection
	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
		handleDisconnection(client);
	}

	// Printing to the terminal and updating the graphical interface to remove the
	// customer from the list
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
	/**
     * Fetches all profile details for a specific subscriber to populate the editor form.
     * * @param message Message containing the subscriber number (String).
     * @param client  The connection thread representing the client making the request.
     */
	public static void handleGetSubscriberDetails(Message message, ConnectionToClient client) {
        String subNumber = (String) message.getData();
        ArrayList<String> subscriberDetails = null;

        String query = "SELECT id, fname, lname, email, phone_number, credit_card_number, family_members " +
                       "FROM gonature_db_new.Subscriber WHERE sub_number = ?";
        try {
            java.sql.Connection conn = DBconnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(subNumber));
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                subscriberDetails = new ArrayList<>();
                subscriberDetails.add(rs.getString("id"));
                subscriberDetails.add(rs.getString("fname"));
                subscriberDetails.add(rs.getString("lname"));
                subscriberDetails.add(rs.getString("email"));
                subscriberDetails.add(rs.getString("phone_number"));
                
                String cc = rs.getString("credit_card_number");
                subscriberDetails.add(cc != null ? cc : "");
                
                subscriberDetails.add(String.valueOf(rs.getInt("family_members"))); // Index 6
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Error fetching subscriber details for editor.");
            e.printStackTrace();
        }

        try {
            client.sendToClient(new Message(MessageType.GET_SUBSCRIBER_DETAILS_RESPONSE, subscriberDetails));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	/**
     * Updates subscriber details in the database based on their subscriber number.
     * Prevents modifying strict fields like personal ID and subscriber number.
     * * @param message Message containing an ArrayList: [0] sub_number (String), [1] fname (String), 
     * [2] lname (String), [3] email (String), [4] phone (String), [5] creditCard (String).
     * @param client  The connection thread representing the client making the request.
     */
	public static void handleUpdateSubscriberDetails(Message message, ConnectionToClient client) {
        ArrayList<String> data = (ArrayList<String>) message.getData();
        String subNumber = data.get(0);
        String email = data.get(3);
        String phone = data.get(4);
        String creditCard = data.get(5);
        String familyMembers = data.get(6); // Family limit fetched sequentially
        
        boolean success = false;
        
        String query = "UPDATE gonature_db_new.Subscriber SET email = ?, phone_number = ?, " +
                       "credit_card_number = ?, family_members = ? WHERE sub_number = ?";
        
        try {
            java.sql.Connection conn = DBconnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, creditCard.isEmpty() ? null : creditCard);
            pstmt.setInt(4, Integer.parseInt(familyMembers));
            pstmt.setInt(5, Integer.parseInt(subNumber));
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
                System.out.println("Server: Subscriber " + subNumber + " updated profile parameters successfully.");
            }
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Error updating subscriber parameters into schema tables.");
            e.printStackTrace();
        }
        
        try { 
            client.sendToClient(new Message(MessageType.UPDATE_SUBSCRIBER_DETAILS_RESPONSE, success)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
	

	// Notification as soon as the server starts listening for connections on the
	// specified port
	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}
}
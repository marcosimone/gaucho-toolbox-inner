import java.io.IOException;
import java.util.*;

import org.jsoup.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
public class Main {
	private static Map<String, String> cookies;
	
	public static void main(String[] args) {
		
		try{
			cookies = getCookies();
		}catch(Exception e){
			System.out.println("Could not connect to GOLD");
			e.printStackTrace();
			System.exit(1);
		}
		Connection grabSchedule;
		
		grabSchedule= Jsoup.connect("https://my.sa.ucsb.edu/gold/StudentSchedule.aspx")
				.cookies(cookies)
				.timeout(5000)
				.userAgent("Mozilla/0.1 App Testing");
		
		
		
		Document schedulePage=null;
		try {
			schedulePage=grabSchedule.get();
			
			if(schedulePage.title().equals("Login")){
				System.out.println("Login failed");
				System.exit(1);
			}
			
		} catch (IOException e) {
			System.out.println("error: grabbing schedule");
			e.printStackTrace();
			System.exit(1);
		}
		
		//id contains courseheading
		Element classTable = schedulePage.getElementById("pageContent_CourseList");
		int classCount=classTable.select("[id*=courseheading]").size();
		
		Course[]  courseArray= new Course[classCount]; 
		
		//format and store data
		Elements exp = classTable.select("td[class*=clcellprimary]");
		for(Element e : exp){
			System.out.println(e.text());
		}
		
		
	}
	
	
	private static Map<String, String> getCookies(){
		
		String username="$USERNAME";
		String password="$PASSWORD";
		String VIEWSTATE;
		String VIEWSTATEGENERATOR;
		String EVENTVALIDATION;
		Map<String, String> cookies = new HashMap<String, String>();																																									
		
		//connect to login page to grab header data
		Document loginPage=null;
		Connection initialConnection=null;	
		try {
			initialConnection = Jsoup.connect("https://my.sa.ucsb.edu/gold/Login.aspx");
			loginPage=initialConnection.get();
			cookies.putAll(initialConnection.response().cookies());
			
		} catch (IOException e) {
			System.out.println("could not connect to Login page");
			e.printStackTrace();
			System.exit(1);
		}
		
		VIEWSTATE=loginPage.select("#__VIEWSTATE").val();
		VIEWSTATEGENERATOR=loginPage.select("#__VIEWSTATEGENERATOR").val();
		EVENTVALIDATION=loginPage.select("#__EVENTVALIDATION").val();
		
		
		//Use header data to log into gold then store cookies
		Response gatherCookies;
		try {
			gatherCookies = Jsoup.connect("https://my.sa.ucsb.edu/gold/Login.aspx")
					.cookies(cookies)
					.data("__LASTFOCUS", "", "__VIEWSTATE", VIEWSTATE, "__VIEWSTATEGENERATOR", VIEWSTATEGENERATOR, "__EVENTTARGET","", "_EVENTARGUMENT","", "__EVENTVALIDATION",EVENTVALIDATION,
						"ctl00$pageContent$userNameText", username, "ctl00$pageContent$passwordText", password, "ctl00$pageContent$loginButton.x", "100", "ctl00$pageContent$loginButton.y", "10", "ctl00$pageContent$PermPinLogin$userNameText","","ctl00$pageContent$PermPinLogin$passwordText","")
					.timeout(5000)
					.userAgent("Mozilla/0.1 App Testing")
					.method(Method.POST)
				    .execute();
			
			
			cookies.putAll(gatherCookies.cookies());
			
		} catch (IOException e) {
			System.out.println("error: gathering cookies");
			e.printStackTrace();
			System.exit(1);
		};
		
		return cookies;
		
	}

}

class Course{
	private String name;
	private int enrollCode;
	private char grading;
	private double units;
	private String professor;
	private String lecture;
	private String lectureRoom;
	private String ta;
	private String section;
	private String sectionRoom;
	private String finalDate;
	
	
	public Course(){}
	
	public Course(String name, int enrollCode, char grading, double units, String professor, String lecture, String lectureRoom, String ta, String section, String sectionRoom, String finalDate){
		this.name=name;
		this.enrollCode=enrollCode;
		this.grading=grading;
		this.units=units;
		this.professor=professor;
		this.lecture=lecture;
		this.lectureRoom=lectureRoom;
		this.ta=ta;
		this.section=section;
		this.sectionRoom=sectionRoom;
		this.finalDate=finalDate;
	}
	
	public int getEnrollCode() {
		return enrollCode;
	}

	public void setEnrollCode(int enrollCode) {
		this.enrollCode = enrollCode;
	}

	public char getGrading() {
		return grading;
	}

	public void setGrading(char grading) {
		this.grading = grading;
	}

	public double getUnits() {
		return units;
	}

	public void setUnits(double units) {
		this.units = units;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}

	public String getLecture() {
		return lecture;
	}

	public void setLecture(String lecture) {
		this.lecture = lecture;
	}

	public String getLectureRoom() {
		return lectureRoom;
	}

	public void setLectureRoom(String lectureRoom) {
		this.lectureRoom = lectureRoom;
	}

	public String getTa() {
		return ta;
	}

	public void setTa(String ta) {
		this.ta = ta;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getSectionRoom() {
		return sectionRoom;
	}

	public void setSectionRoom(String sectionRoom) {
		this.sectionRoom = sectionRoom;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	@Override
	public String toString() {
		return "Course [name=" + name + ", enrollCode=" + enrollCode
				+ ", grading=" + grading + ", units=" + units + ", professor="
				+ professor + ", lecture=" + lecture + ", lectureRoom="
				+ lectureRoom + ", ta=" + ta + ", section=" + section
				+ ", sectionRoom=" + sectionRoom + ", finalDate=" + finalDate
				+ "]";
	}
	
	
	
}
























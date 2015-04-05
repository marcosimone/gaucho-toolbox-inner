import java.io.IOException;
import java.util.*;

import org.jsoup.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.*;
public class Main {

	public static void main(String[] args) {
		
		//INSERT INFO TO TEST
		String username="USERNAME";
		String password="PASSWORD";
		
		
		Document loginPage=null;
		Connection initialConnection=null;
		Map<String, String> cookies = new HashMap<String, String>();
		try {
			initialConnection = Jsoup.connect("https://my.sa.ucsb.edu/gold/Login.aspx");
			loginPage=initialConnection.get();
			cookies.putAll(initialConnection.response().cookies());
			
		} catch (IOException e) {
			System.out.println("could not connect to Login page");
			e.printStackTrace();
			System.exit(1);
		}
		
		if(loginPage.title().equals("Login")){
			System.out.println("Login failed");
			System.exit(0);
		}
		String VIEWSTATE;
		String VIEWSTATEGENERATOR;
		String EVENTVALIDATION;
		
		VIEWSTATE=loginPage.select("#__VIEWSTATE").val();
		//System.out.println(VIEWSTATE);
		VIEWSTATEGENERATOR=loginPage.select("#__VIEWSTATEGENERATOR").val();
		//System.out.println(VIEWSTATEGENERATOR);
		EVENTVALIDATION=loginPage.select("#__EVENTVALIDATION").val();
		//System.out.println(EVENTVALIDATION);
		
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
		
		
		Connection grabSchedule;
		
		grabSchedule= Jsoup.connect("https://my.sa.ucsb.edu/gold/StudentSchedule.aspx")
				.cookies(cookies)
				.timeout(5000)
				.userAgent("Mozilla/0.1 App Testing");
		Document schedulePage=null;
		try {
			schedulePage=grabSchedule.get();
		} catch (IOException e) {
			System.out.println("error: grabbing schedule");
			e.printStackTrace();
			System.exit(1);
		}
		
		
		
		
	}

}

class Course{
	private String name;
	private String professor;
	private String lecture;
	private String lectureRoom;
	private String ta;
	private String section;
	private String sectionRoom;
	private String finalDate;
	
	Course(String name, String professor, String lecture, String lectureRoom, String ta, String section, String sectionRoom, String finalDate){
		this.name=name;
		this.professor=professor;
		this.lecture=lecture;
		this.lectureRoom=lectureRoom;
		this.ta=ta;
		this.section=section;
		this.sectionRoom=sectionRoom;
		this.finalDate=finalDate;
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
		return "Course [name=" + name + ", professor=" + professor
				+ ", lecture=" + lecture + ", lectureRoom=" + lectureRoom
				+ ", ta=" + ta + ", section=" + section + ", sectionRoom="
				+ sectionRoom + ", finalDate=" + finalDate + "]";
	}
	
	
	
}
























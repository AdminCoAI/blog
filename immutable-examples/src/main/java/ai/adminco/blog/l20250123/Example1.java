package ai.adminco.blog.l20250123;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Premium Calculator that calculates the following:
 *
 * Premium Tax (Front End):
 *   1. If the Issue State has a front end Premium Tax -- California, Pennsylvania, and New York
 *   2. Otherwise it's $0
 *   3. Gross Premium times the Premium Tax Rate
 *
 * Premium Tax Rate = 2% for all state except California, California = 4%
 *
 * Premium Tax (Back End);
 *   1. If the Issue State has a back end Premium Tax -- California, Montana
 *   2. Otherwise it's $0
 *   3. Gross Premium times the Premium Tax Rate
 *
 * 5498 Premium:
 *   1. If the policy is a qualified policy
 *   2. Equal to the Gross Premium
 *   3. Otherwise it's $0
 *
 * First Year Premium:
 *   True if the policy year = 1
 *
 * Net Premium:
 *   Gross Premium - Premium Tax (Front)
 *
 *
 */
public class Example1 {
   public static class Answer {
	  public static class ImmutablePremiumCalculator {
		 private final BigDecimal grossPremium;
		 private final State issueState;
		 private final Integer policyYear;
		 private final QualifiedType qualifiedType;

		 public ImmutablePremiumCalculator(
			   State issueState,
			   QualifiedType qualifiedType,
			   Integer policyYear,
			   BigDecimal grossPremium) {
			this.issueState = issueState;
			this.qualifiedType = qualifiedType;
			this.policyYear = policyYear;
			this.grossPremium = grossPremium;
		 }

		 public BigDecimal getGrossPremium() {
			return grossPremium;
		 }

		 public State getIssueState() {
			return issueState;
		 }

		 public BigDecimal getNetPremium() {
			return getGrossPremium().subtract( getPremiumTaxFrontEnd() );
		 }

		 public Integer getPolicyYear() {
			return policyYear;
		 }

		 public BigDecimal getPremium5498() {
			return isQualified() ? getGrossPremium() : BigDecimal.ZERO;
		 }

		 public BigDecimal getPremiumTaxBackEnd() {
			return hasBackendPremiumTax()
				  ? getGrossPremium().multiply( getPremiumTaxRate() )
				  : BigDecimal.ZERO;
		 }

		 public BigDecimal getPremiumTaxFrontEnd() {
			return hasFrontEndPremiumTax()
				  ? getGrossPremium().multiply( getPremiumTaxRate() )
				  : BigDecimal.ZERO;
		 }

		 public BigDecimal getPremiumTaxRate() {
			return State.CALIFORNIA.equals( getIssueState() )
				  ? BigDecimal.valueOf( 0.04 )
				  : BigDecimal.valueOf( 0.02 );
		 }

		 public QualifiedType getQualifiedType() {
			return qualifiedType;
		 }

		 public boolean hasBackendPremiumTax() {
			return Arrays.asList( State.CALIFORNIA, State.MONTANA )
				  .contains( getIssueState() );
		 }

		 public boolean hasFrontEndPremiumTax() {
			return Arrays.asList( State.CALIFORNIA, State.PENNSYLVANIA, State.NEW_YORK )
				  .contains( getIssueState() );
		 }

		 public boolean isFirstYear() {
			return 1 == getPolicyYear();
		 }

		 public boolean isQualified() {
			return getQualifiedType().isQualified();
		 }
	  }
   }

   public static class ImmutablePremiumCalculator {
	  private final BigDecimal grossPremium;
	  private final State issueState;
	  private final Integer policyYear;
	  private final QualifiedType qualifiedType;

	  public ImmutablePremiumCalculator(
			State issueState,
			QualifiedType qualifiedType,
			Integer policyYear,
			BigDecimal grossPremium) {
		 this.issueState = issueState;
		 this.qualifiedType = qualifiedType;
		 this.policyYear = policyYear;
		 this.grossPremium = grossPremium;
	  }

	  public BigDecimal getGrossPremium() {
		 return grossPremium;
	  }

	  public State getIssueState() {
		 return issueState;
	  }

	  public BigDecimal getNetPremium() {
		 throw new RuntimeException( "Not Implemented Yet" );
	  }

	  public Integer getPolicyYear() {
		 return policyYear;
	  }

	  public BigDecimal getPremium5498() {
		 throw new RuntimeException( "Not Implemented Yet" );
	  }

	  public BigDecimal getPremiumTaxBackEnd() {
		 throw new RuntimeException( "Not Implemented Yet" );
	  }

	  public BigDecimal getPremiumTaxFrontEnd() {
		 throw new RuntimeException( "Not Implemented Yet" );
	  }

	  public QualifiedType getQualifiedType() {
		 return qualifiedType;
	  }

	  public boolean isFirstYear() {
		 throw new RuntimeException( "Not Implemented Yet" );
	  }
   }

   public static class ImmutableRun1 {

	  public static void main(String[] args) {
		 final State state = State.PENNSYLVANIA;
		 final QualifiedType qualifiedType = QualifiedType.IRA;
		 final Integer policyYear = 1;
		 final BigDecimal grossPremium = new BigDecimal( 1000 );

		 final ImmutablePremiumCalculator calculator = new ImmutablePremiumCalculator(
			   state, qualifiedType, policyYear, grossPremium );

		 System.out.println( String.format( "Gross Premium: %s",
			   calculator.getGrossPremium() ) );
		 System.out.println( String.format( "Premium Tax (Front): %s",
			   calculator.getPremiumTaxFrontEnd() ) );
		 System.out.println( String.format( "Premium Tax (Back): %s",
			   calculator.getPremiumTaxBackEnd() ) );
		 System.out.println( String.format( "Net Premium: %s",
			   calculator.getNetPremium() ) );
		 System.out.println( String.format( "5498 Premium: %s",
			   calculator.getPremium5498() ) );
		 System.out.println( String.format( "First Year: %s",
			   calculator.isFirstYear() ) );
	  }
   }

   public static class MutablePremiumCalculator {
	  private boolean firstYear;
	  private BigDecimal grossPremium;
	  private State issueState;
	  private BigDecimal netPremium;
	  private Integer policyYear;
	  private BigDecimal premium5498;
	  private BigDecimal premiumTaxBackEnd;
	  private BigDecimal premiumTaxFrontEnd;
	  private BigDecimal premiumTaxRate;
	  private QualifiedType qualifiedType;

	  public void calculate() {
		 // Premium Tax: Front End
		 premiumTaxFrontEnd = BigDecimal.ZERO;
		 if (hasFrontEndPremiumTax()) {
			premiumTaxFrontEnd = getGrossPremium().multiply( getPremiumTaxRate() );
		 }

		 // Premium Tax: Back End
		 premiumTaxBackEnd = BigDecimal.ZERO;
		 if (hasBackendPremiumTax()) {
			premiumTaxBackEnd = getGrossPremium().multiply( getPremiumTaxRate() );
		 }

		 // Net Premium
		 netPremium = getGrossPremium().subtract( getPremiumTaxFrontEnd() );

		 // 5498
		 premium5498 = BigDecimal.ZERO;
		 if (isQualified()) {
			premium5498 = getGrossPremium();
		 }
	  }

	  public BigDecimal getGrossPremium() {
		 return grossPremium;
	  }

	  public State getIssueState() {
		 return issueState;
	  }

	  public BigDecimal getNetPremium() {
		 return netPremium;
	  }

	  public Integer getPolicyYear() {
		 return policyYear;
	  }

	  public BigDecimal getPremium5498() {
		 return premium5498;
	  }

	  public BigDecimal getPremiumTaxBackEnd() {
		 return premiumTaxBackEnd;
	  }

	  public BigDecimal getPremiumTaxFrontEnd() {
		 return premiumTaxFrontEnd;
	  }

	  public BigDecimal getPremiumTaxRate() {
		 return premiumTaxRate;
	  }

	  public QualifiedType getQualifiedType() {
		 return qualifiedType;
	  }

	  public boolean hasBackendPremiumTax() {
		 return Arrays.asList( State.CALIFORNIA, State.MONTANA )
			   .contains( getIssueState() );
	  }

	  public boolean hasFrontEndPremiumTax() {
		 return Arrays.asList( State.CALIFORNIA, State.PENNSYLVANIA, State.NEW_YORK )
			   .contains( getIssueState() );
	  }

	  public void init() {

		 // Initialize Premium Tax Rate By State
		 if (State.CALIFORNIA.equals( getIssueState() )) {
			premiumTaxRate = BigDecimal.valueOf( 0.04 );
		 } else {
			premiumTaxRate = BigDecimal.valueOf( 0.02 );
		 }

		 // First Year
		 firstYear = 1 == getPolicyYear();
	  }

	  public boolean isFirstYear() {
		 return firstYear;
	  }

	  public boolean isQualified() {
		 return qualifiedType.isQualified();
	  }

	  public void setGrossPremium(BigDecimal grossPremium) {
		 this.grossPremium = grossPremium;
	  }

	  public void setIssueState(State issueState) {
		 this.issueState = issueState;
	  }

	  public void setPolicyYear(Integer policyYear) {
		 this.policyYear = policyYear;
	  }

	  public void setQualifiedType(QualifiedType qualifiedType) {
		 this.qualifiedType = qualifiedType;
	  }

   }

   public static class MutableRun1 {
	  public static void main(String[] args) {
		 final MutablePremiumCalculator calculator = new MutablePremiumCalculator();
		 calculator.setIssueState( State.PENNSYLVANIA );
		 calculator.setGrossPremium( new BigDecimal( 1000 ) );
		 calculator.setPolicyYear( 1 );
		 calculator.setQualifiedType( QualifiedType.IRA );

		 // Temporal Coupling
		 calculator.init();
		 calculator.calculate();

		 System.out.println( String.format( "Gross Premium: %s",
			   calculator.getGrossPremium() ) );
		 System.out.println( String.format( "Premium Tax (Front): %s",
			   calculator.getPremiumTaxFrontEnd() ) );
		 System.out.println( String.format( "Premium Tax (Back): %s",
			   calculator.getPremiumTaxBackEnd() ) );
		 System.out.println( String.format( "Net Premium: %s",
			   calculator.getNetPremium() ) );
		 System.out.println( String.format( "5498 Premium: %s",
			   calculator.getPremium5498() ) );
		 System.out.println( String.format( "First Year: %s",
			   calculator.isFirstYear() ) );
	  }
   }

   public static enum QualifiedType {
	  IRA(true),
	  NQ(false),
	  Roth(true),
	  SEP(true),
	  TSA(true),;

	  private final boolean qualified;

	  private QualifiedType(boolean qualified) {
		 this.qualified = qualified;
	  }

	  public boolean isQualified() {
		 return qualified;
	  }

	  @Override
	  public String toString() {
		 return name();
	  }
   }

   public static enum State {
	  ALABAMA("Alabama", "AL"),
	  ALASKA("Alaska", "AK"),
	  AMERICAN_SAMOA("American Samoa", "AS"),
	  ARIZONA("Arizona", "AZ"),
	  ARKANSAS("Arkansas", "AR"),
	  CALIFORNIA("California", "CA"),
	  COLORADO("Colorado", "CO"),
	  CONNECTICUT("Connecticut", "CT"),
	  DELAWARE("Delaware", "DE"),
	  DISTRICT_OF_COLUMBIA("District of Columbia", "DC"),
	  FLORIDA("Florida", "FL"),
	  GEORGIA("Georgia", "GA"),
	  GUAM("Guam", "GU"),
	  HAWAII("Hawaii", "HI"),
	  IDAHO("Idaho", "ID"),
	  ILLINOIS("Illinois", "IL"),
	  INDIANA("Indiana", "IN"),
	  IOWA("Iowa", "IA"),
	  KANSAS("Kansas", "KS"),
	  KENTUCKY("Kentucky", "KY"),
	  LOUISIANA("Louisiana", "LA"),
	  MAINE("Maine", "ME"),
	  MARYLAND("Maryland", "MD"),
	  MASSACHUSETTS("Massachusetts", "MA"),
	  MICHIGAN("Michigan", "MI"),
	  MINNESOTA("Minnesota", "MN"),
	  MISSISSIPPI("Mississippi", "MS"),
	  MISSOURI("Missouri", "MO"),
	  MONTANA("Montana", "MT"),
	  NEBRASKA("Nebraska", "NE"),
	  NEVADA("Nevada", "NV"),
	  NEW_HAMPSHIRE("New Hampshire", "NH"),
	  NEW_JERSEY("New Jersey", "NJ"),
	  NEW_MEXICO("New Mexico", "NM"),
	  NEW_YORK("New York", "NY"),
	  NORTH_CAROLINA("North Carolina", "NC"),
	  NORTH_DAKOTA("North Dakota", "ND"),
	  OHIO("Ohio", "OH"),
	  OKLAHOMA("Oklahoma", "OK"),
	  OREGON("Oregon", "OR"),
	  PENNSYLVANIA("Pennsylvania", "PA"),
	  PUERTO_RICO("Puerto Rico", "PR"), // Note: Not a state but included for completeness
	  RHODE_ISLAND("Rhode Island", "RI"),
	  SOUTH_CAROLINA("South Carolina", "SC"),
	  SOUTH_DAKOTA("South Dakota", "SD"),
	  TENNESSEE("Tennessee", "TN"),
	  TEXAS("Texas", "TX"),
	  UTAH("Utah", "UT"),
	  VERMONT("Vermont", "VT"),
	  VIRGINIA("Virginia", "VA"),
	  WASHINGTON("Washington", "WA"),
	  WEST_VIRGINIA("West Virginia", "WV"),
	  WISCONSIN("Wisconsin", "WI"),
	  WYOMING("Wyoming", "WY");

	  private final String abbreviation;
	  private final String fullName;

	  State(String fullName, String abbreviation) {
		 this.fullName = fullName;
		 this.abbreviation = abbreviation;
	  }

	  public String getAbbreviation() {
		 return abbreviation;
	  }

	  public String getFullName() {
		 return fullName;
	  }
   }

}

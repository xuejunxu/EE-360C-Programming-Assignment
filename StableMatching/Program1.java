/*
 * Name: Xuejun Xu
 * EID:
 */
/*
Name: Xuejun Xu
EID: xx2757
 */

import java.util.ArrayList;
//import

/**
 * Your solution goes in this class.
 * <p>
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * <p>
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching marriage) {
        /* TODO implement this function */
        boolean result = true;
        int res_n = marriage.getResidentCount();
        int hos_m = marriage.getHospitalCount();

        // get the current stable matching for the input Matching class marriage

        // see whether all slots are filled
        int num_matched_res = 0;

        int total_slots = marriage.totalHospitalSlots();

        ArrayList<Integer> current_matching_list = marriage.getResidentMatching();

        for (int count = 0; count < current_matching_list.size(); count++) {
            int matched_result = current_matching_list.get(count);
            if (matched_result != -1) {
                num_matched_res = num_matched_res + 1;
            }
        }

//        if (num_matched_res != res_n) {
//            result = false;
//        }


        for (int i = 0; i < res_n; i++) {

            ArrayList<Integer> current_match = marriage.getResidentMatching();

            int matched_hospital_index = current_match.get(i); //get the matched hospital index

            if (matched_hospital_index != -1) {
                //get the preference list of the matched hospital with matched_hospital_index
                ArrayList<Integer> matched_hospital_prelist = marriage.getHospitalPreference().get(matched_hospital_index);

                //get the resident's rank in the currently matched hospital's preference list
                int resident_rank = matched_hospital_prelist.indexOf(i);
                //resident_rank gets the ranking of resident in the currently matched hospital's preference list

                // to compare the residents rank higher in the currently matched hospital's list
                for (int j = 0; j < resident_rank; j++) {
                    //to get the current matching of every resident that ranks higher at the hospital's preference list
                    //first to get the index of the residents rank higher
                    int resident_higher_index = matched_hospital_prelist.get(j);

                    int resident_rankhigher = marriage.getResidentMatching().get(resident_higher_index);

                    //if the higher_rank resident is not matched to anyone, then the matching is not stable
                    if (resident_rankhigher == -1) {
                        result = false;
                        break;
                    } else {
                        // If there's a pair where they could switch to a better match, then the matching is unstable
                        //to get the preference list of the resident ranks hihger at the hospital's preference list's
                        // to see whether the current match of the resident ranks higher ranks higher than the current
                        //hospital
                        ArrayList<ArrayList<Integer>> resident_all_prelist = marriage.getResidentPreference();
                        ArrayList<Integer> resident_higher_prelist = resident_all_prelist.get(resident_higher_index);
                        int rank_h_k = resident_higher_prelist.indexOf(resident_rankhigher);
                        int rank_h_i = resident_higher_prelist.indexOf(matched_hospital_index);
                        if (rank_h_i < rank_h_k) {
                            result = false;
                            break;
                        }

                    }
                }
            } else {
                continue;
            }

        }
        //return false; /* TODO remove this line */
        return result;
    }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageBruteForce_residentoptimal(Matching marriage) {
        /* TODO implement this function */
        int n = marriage.getResidentCount();
        int slots = marriage.totalHospitalSlots();
        int m = marriage.getResidentCount();

        //use an ArrayList to store all the stable matchings
        ArrayList<Matching> all_stable_matching_list = new ArrayList<Matching>();

        Permutation p = new Permutation(n, slots); //a Permutaion class of object
        Matching matching;

        matching = p.getNextMatching(marriage);

        Matching resident_optimal_matching = matching;
        int initial_ind = 0;

        //get all possible matchings
        while (matching != null) {
            if (isStableMatching(matching)) {
                all_stable_matching_list.add(matching);
                ArrayList<Integer> current_res_matching = matching.getResidentMatching();
                int sum_of_index = 0;
                for (int count = 0; count < n; count++) {
                    if (current_res_matching.get(count) != -1) {
                        ArrayList<Integer> current_res_prelist = matching.getResidentPreference().get(count);
                        int rank_of_count = current_res_prelist.indexOf(current_res_matching.get(count));
                        sum_of_index = sum_of_index + m - rank_of_count + 1;

                    }
                }
                if (initial_ind < sum_of_index) {
                    initial_ind = sum_of_index; //if getting a higher value then switch
                    resident_optimal_matching = matching;
                }
            }
            matching = p.getNextMatching(matching);
        }

        return resident_optimal_matching;

        //return null; /* TODO remove this line */
    }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_residentoptimal(Matching marriage) {
        /* TODO implement this function */

        int res_n = marriage.getResidentCount();
        int hos_m = marriage.getHospitalCount();

        //initialize an ArrayList ini_res_match to store provide an initialization with all residents unmatched
        ArrayList<Integer> ini_res_match = new ArrayList<>();
        for (int num = 0; num < res_n; num++) {
            ini_res_match.add(-1);
        }

        // create an ArrayList to store available slots for each hospital h_i
        // initialize the ArrayList to be maximum slots for each hospital h_i
        ArrayList<Integer> available_slots = marriage.getHospitalSlots();

        // create an ArrayList to store the number of hospitals that have proposed to
        ArrayList<Integer> propose_list = new ArrayList<Integer>();
        // the index is the index for the hospital, and the value in ArrayList is the
        // number of hospitals they have proposed to

        //Initialize all of them to be zero, every resident should have a number that they still
        // need to propose to
        for (int g = 0; g < res_n; g++) {
            propose_list.add(hos_m);
        }

        // create an integer variable to store the total available slots
        int total_available_slots = marriage.totalHospitalSlots();

        for (int a = 0; a < res_n; a++) {

            for (int i = 0; i < res_n; i++) {//for every student of size n

                //get the index of matching for the i-th student r_i (resident's index is i)
                int number_needs_to_propose = propose_list.get(i);

                //while (number_needs_to_propose > 0) {

                int r_i_match = ini_res_match.get(i);

                //if the student is currently available

                if (number_needs_to_propose > 0) {
                    if (r_i_match == -1) {

                        //When the student is currently available, we go over r_i's preference list
                        // use a propose list to store the hospitals that r_i has not yet proposed
                        //ArrayList<Integer> r_i_proposelist = marriage.getResidentPreference().get(i);
                        ArrayList<Integer> r_i_prelist = marriage.getResidentPreference().get(i);

                        // go over all hospitals that r_i hasn't proposed yet in r_i's preference list
                        //int number_needs_to_propose = hos_m - propose_list.get(i);

                        for (int k = hos_m - number_needs_to_propose; k < hos_m; k++) {
                            //get the absolute index for hospital h_k

                            int num_i_not_proposed = propose_list.get(i);
                            propose_list.set(i, num_i_not_proposed - 1);

                            int h_k_index = r_i_prelist.get(k);

                            int h_k_current_slot = available_slots.get(h_k_index);

                            //if h_k available slots>=1
                            if (h_k_current_slot >= 1) {
                                //then h_k and r_i are matched
                                ini_res_match.set(i, h_k_index);

                                //totalslots-1, h_k available slots -1
                                total_available_slots = total_available_slots - 1;
                                available_slots.set(h_k_index, (h_k_current_slot - 1));
                                break;

                            } else {
                                //else h_k is not available

                                //get the current matches of h_k

                                ArrayList<Integer> h_k_current_matches = new ArrayList<Integer>();
                                for (int z = 0; z < res_n; z++) {
                                    if (ini_res_match.get(z) == h_k_index) {
                                        h_k_current_matches.add(z);
                                    }
                                }

                                //see where is r_i in h_k's preference list
                                ArrayList<Integer> h_k_prelist = marriage.getHospitalPreference().get(h_k_index);

                                int r_i_in_h_k_list = h_k_prelist.indexOf(i);

                                //now h_k_current_matches store all the matches of h_k right now

                                for (int current_matches = 0; current_matches < h_k_current_matches.size(); current_matches++) {
                                    //while(){
                                    // The absolute index of the current match of h_k
                                    int current_match_index = h_k_current_matches.get(current_matches);
                                    int r_current_match_in_list = h_k_prelist.indexOf(current_match_index);
                                    if (r_i_in_h_k_list < r_current_match_in_list) {
                                        //break;
                                        //if r_i ranks higher than a current resident
                                        //then current match is dismatched, and h_k is matched to r_i
                                        ini_res_match.set(i, h_k_index);
                                        ini_res_match.set(current_match_index, -1);
                                        break;
                                        //}
                                    }
//                                    ini_res_match.set(i, h_k_index);
//                                    ini_res_match.set(current_match_index, -1);
                                }
                                // }
                            }
                        }
                    }
                }
            }
        }

        marriage.setResidentMatching(ini_res_match);

        //return null; /* TODO remove this line */
        return marriage;
    }


    /**
     * Determines a hospital optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */

    @Override
    public Matching stableMarriageGaleShapley_hospitaloptimal(Matching marriage) {
        /* TODO implement this function */

        int res_n = marriage.getResidentCount();
        int hos_m = marriage.getHospitalCount();

        //initialize an ArrayList ini_res_match to store provide an initialization with all residents unmatched
        ArrayList<Integer> ini_res_match = new ArrayList<>();
        for (int num = 0; num < res_n; num++) {
            ini_res_match.add(-1);
        }

        ArrayList<Integer> available_slots = marriage.getHospitalSlots();

        // create a ArrayList to store available slots for each hospital h_i
        // initialize the ArrayList to be maximum slots for each hospital h_i

        int total_available_slots = marriage.totalHospitalSlots();

        while (total_available_slots > 0) {
            //System.out.println("The totoal available slots is" + total_available_slots);
            for (int a = 0; a < hos_m; a++) {
                //for a hospital h_a, get it's preferencelist
                ArrayList<Integer> h_a_prelist = marriage.getHospitalPreference().get(a);

                for (int b = 0; b < res_n; b++) {
                    // if the hospital is currently available
                    if (available_slots.get(a) > 0) {
                        //check every resident r_b in h_a's preference list
                        int r_b_index = h_a_prelist.get(b);
                        //get the current matching for r_b
                        int r_b_current_match = ini_res_match.get(r_b_index);

                        // if r_b is not matched to anyone right now
                        if (r_b_current_match == -1) {
                            //then h_a is matched to r_b
                            ini_res_match.set(r_b_index, a);
                            //total_available_slots -1
                            total_available_slots = total_available_slots - 1;
                            // available slots of h_a -1
                            int ava_slots_a = available_slots.get(a);
                            available_slots.set(a, ava_slots_a - 1);
                        }
                        //else when r_b is matched to someone right now
                        else {
                            //if r_b prefers h_a to its current match, then r_b and h_a are matched,;
                            //get r_b's preference list
                            ArrayList<Integer> r_b_prelist = marriage.getResidentPreference().get(r_b_index);

                            //if h_a ranks higher than current match
                            int h_a_rank_in_r_b = r_b_prelist.indexOf(a);
                            int current_match_in_r_b = r_b_prelist.indexOf(r_b_current_match);
                            if (h_a_rank_in_r_b < current_match_in_r_b) {
                                //then h_a and r_b are matched, current match available slots+1, h_a available slots -1
                                ini_res_match.set(r_b_index, a);
                                int ava_slots_a1 = available_slots.get(a);
                                available_slots.set(a, ava_slots_a1 - 1);

                                int ava_slots_cm = available_slots.get(r_b_current_match);
                                available_slots.set(r_b_current_match, ava_slots_cm + 1);

                                //total available slots remain the same
                            }
                        }
                    }
                }
            }
        }

        //to set the stable matching into the matching object marriage
        marriage.setResidentMatching(ini_res_match);

        //return null; /* TODO remove this line */
        return marriage;
    }
}

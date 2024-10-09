package com.smart.controller;

import com.smart.helper.Message;
import com.smart.model.Contact;
import com.smart.model.User;
import com.smart.repo.UserRepository;
import com.smart.repo.contactRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private contactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        model.addAttribute("user", userRepository.findByEmail(principal.getName()));
    }

    // dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // Open add contact form
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
    return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 Principal principal,
                                 @RequestParam("profileImage")MultipartFile file,
                                 HttpSession session){
        try {
            User user = userRepository.findByEmail(principal.getName());

            // Process and save image
            if (file.isEmpty()) {
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path  = Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            contact.setUser(user);
            user.getContacts().add(contact);
            userRepository.save(user);

            // message success
            session.setAttribute("message", new Message("Contact added successfully !!", "success"));
        }catch (Exception e){
            session.setAttribute("message", new Message("Something went wrong !! Try again.", "danger"));
        }
        return "normal/add_contact_form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page,
                               Model model,
                               Principal principal){
        User user = userRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(page, 10);
        Page<Contact> contacts = contactRepository.getContactByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show-contacts";
    }

    // Show contact details
    @GetMapping("contact/{cid}")
    public String showContactDetails(@PathVariable int cid, Model model, Principal principal){
        Optional<Contact> contactOptional = contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        //
        User user = userRepository.findByEmail(principal.getName());
        if(user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title",contact.getName());
        }
    return "normal/show_contact_details";
    }

    // Delete contact
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") int cid, Principal principal, HttpSession session){
        Optional<Contact> contactOptional = contactRepository.findById(cid);
        Contact contact = contactOptional.get();
        User user = userRepository.findByEmail(principal.getName());
        if(user.getId() == contact.getUser().getId()) {
            File file = new File("target/classes/static/img/"+contact.getImage());
            file.delete();
            contactRepository.delete(contact);
            session.setAttribute("message", new Message("Contact deleted successfully...", "success"));
        }
        return "redirect:/user/show-contacts/0";
    }

    // Update Contact
    @PostMapping("/update-contact/{cid}")
    public String updateContact(@PathVariable("cid") int cid, Model model){
        Contact contact = contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normal/update_contact";
    }

    // Process for update contact
    @PostMapping("/process-update/{cid}")
    public String processUpdate(@PathVariable("cid") Integer cid,
                                @ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                HttpSession session){
        try{
        Contact oldContact = contactRepository.findById(cid).get();
        oldContact.setName(contact.getName());
        oldContact.setSecondName(contact.getSecondName());
        oldContact.setEmail(contact.getEmail());
        oldContact.setPhone(contact.getPhone());
        oldContact.setWork(contact.getWork());
        oldContact.setDescription(contact.getDescription());
        if (file.isEmpty()) {
            oldContact.setImage(oldContact.getImage());
        } else {
            File oldImage = new File("target/classes/static/img/"+oldContact.getImage());
            oldImage.delete();

            File saveFile = new ClassPathResource("static/img").getFile();
            Path path  = Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            oldContact.setImage(file.getOriginalFilename());
        }
            contactRepository.save(oldContact);
        session.setAttribute("message", new Message("Contact update successfully !!", "success"));
    }catch (Exception e){
        session.setAttribute("message", new Message("Something went wrong !! Try again.", "danger"));
    }
        return "redirect:/user/contact/"+contact.getCid();
    }

    // Profile
    @GetMapping("/profile")
    public String profile(Model model, Principal principal){
        model.addAttribute("title", "Profile");
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "normal/profile";
    }


}

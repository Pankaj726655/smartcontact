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
import java.util.List;
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
            session.setAttribute("message", new Message("Contact added successful !!", "success"));
        }catch (Exception e){
            System.out.println("ERROR : "+ e);
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


}

package com.example.springedu2.controller;

import com.example.springedu2.entity.Visitor;
import com.example.springedu2.repository.VisitorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.Bidi;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VisitorController {

    // 1. @Autowired 이용 생성자 주입
    // @Autowired
    // private VisitorRepository visitorRepository;

    // 2. 생성자 주입 : 요즘 방식
    // private VisitorRepository visitorRepository;
    // public  VisitorController(VisitorRepository visitorRepository) {
    //    this.visitorRepository = visitorRepository;
    // }

    // 3. 생성자 주입 다른 방법, final 가능
    // @RequiredArgsConstructor 필수 : Lombok 필수
    private final VisitorRepository visitorRepository;

    // vlist 방명록 조회
    @GetMapping("/vlist")
    public ModelAndView vlist() {
        List<Visitor> visitors = visitorRepository.findAll(); // 목록 조회
        return visitorView(visitors, null);
    }

    // visitorView 함수
    private ModelAndView visitorView(List<Visitor> visitors, String buttonText) {
        ModelAndView mv = new ModelAndView("visitorView");
        // mv.setViewName("visitorView");   // visitorView.html(Model 사용) - thymeleaf
        if (visitors.isEmpty()) {
            mv.addObject("msg", "조회된 결과가 없습니다.");
        } else {
            mv.addObject("vList", visitors);
        }
        if (buttonText != null) {
            mv.addObject("buttonText", buttonText);
        }
        return mv;
    }


    // vinsert : 방명록 추가
    // @Valid : form 에서 넘어온 자료를 @Entity 에 있는
    // 설정( @Id, @NotBlank, @Column(nullable=false) )
    // 과 비교해서, 입력 data를 검증한다
    @PostMapping("/vinsert")
    @Transactional
    public String vinsert(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model) {

        System.out.println("visitor:" + visitor);
        System.out.println("bindingResult:" + bindingResult);
        if(bindingResult.hasErrors()) {
            model.addAttribute("msg", "이름과 내용을 모두 입력하세요");
            return "visitorView"; // visitorView.html
        }
        visitorRepository.save(visitor); // entity 객체를 사용해야한다.

        return "redirect:/vlist";
    }

    // /one 방명록 id 로 조회 : Rest 방식 호출 -> 결과 : json
    // return 값이 visitor 객체일때 json 으로 변환되어 다운로드된다.
    // return 값이 ResponseEntity<Visitor> 일때는 data는 json으로 상태코드로 리턴가능
    @GetMapping(value = "/one", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Visitor> one(@RequestParam Integer id) {
        return visitorRepository.findById(Long.valueOf(id))  // data를 id 로 조회해서 있으면 Visitor 리턴
                .map(ResponseEntity::ok)                     // 상태코드 ok 200 을 추가해서 리턴
                .orElseGet(() -> ResponseEntity.notFound().build() );
                      // 못찾으면 null 대신, 404 코드를 객체로 바꾸어서(.build()) 리턴
    }

    // /vupdate 수정
    /*
    @PostMapping("/vupdate")
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 모두 입력하세요");
            return "redirect:/vlist";
        }
        // 수정
        visitorRepository.save(visitor);
        return "redirect:/vlist";
    } */

    // /vupdate 수정
    @PostMapping("/vupdate")
    @Transactional    // 필수 (context의 내용 수정후 커밋을 진행 하여, DB와의 차이를 찾아서 변경함)
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 모두 입력하세요");
            return "redirect:/vlist";
        }
        Visitor entity  = visitorRepository.findById(Long.valueOf(visitor.getId()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방명록 입니다"));
        entity.setName(visitor.getName());
        entity.setMemo(visitor.getMemo());
        return "redirect:/vlist";

    }

    // /vdelete 삭제
    @PostMapping("/vdelete")
    @Transactional
    public String delete(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        if ( !visitorRepository.existsById(Long.valueOf(id)) ) {
            redirectAttributes.addFlashAttribute("msg", "삭제할 방명록을 찾을 수 없습니다.");
            return "redirect:/vlist";
        }
        visitorRepository.deleteById(Long.valueOf(id));
        return "redirect:/vlist";
    }

    // /vsearch
    // findByMemoContainingIgnoreCaseOrderByIdDesc(key)
    // 검색 : 모두 대문자로 검색어를 포함한 data
    // 단, 정렬은 id 를 내림차순으로 출력한다
    @GetMapping("/vsearch")
    public ModelAndView vsearch(@RequestParam(defaultValue = "") String key) {
        List<Visitor> visitors = key.isBlank()
                ? visitorRepository.findAll()
        //        : visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
                : visitorRepository.findByIrum(key);
        System.out.println( visitors );
        return visitorView(visitors, "메인으로 돌아가기");
    }



} // Controller End

